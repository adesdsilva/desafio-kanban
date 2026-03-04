package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.BusinessException;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.mapper.ProjectMapper;
import br.com.setecolinas.kanban_project.model.Project;
import br.com.setecolinas.kanban_project.model.enums.ProjectStatus;
import br.com.setecolinas.kanban_project.repository.ProjectRepository;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import br.com.setecolinas.kanban_project.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository repo;
    private final ResponsibleRepository respRepo;

    public ProjectService(ProjectRepository repo, ResponsibleRepository respRepo) {
        this.repo = repo;
        this.respRepo = respRepo;
    }

    // ====== Utilitário para MDC + userId ======
    private <T> T withUserContext(Supplier<T> action) {
        MDC.put("userId", getCurrentUserId());
        try {
            return action.get();
        } finally {
            MDC.remove("userId");
        }
    }

    private void withUserContext(Runnable action) {
        MDC.put("userId", getCurrentUserId());
        try {
            action.run();
        } finally {
            MDC.remove("userId");
        }
    }

    private String getCurrentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "system";
        final var principal = auth.getPrincipal();
        if (principal instanceof String) return principal.toString();
        return auth.getName();
    }

    // ====== CRUD ======
    @Transactional(readOnly = true)
    public ProjectResponseDTO findById(Long id) {
        return withUserContext(() -> {
            log.info("action=findById.started id={}", id);
            String tenantId = TenantContext.getCurrentTenantId();
            ProjectResponseDTO dto = repo.findByIdAndTenantId(id, tenantId)
                    .map(ProjectMapper::toResponse)
                    .orElseThrow(() -> new NotFoundException("Project not found"));
            log.info("action=findById.finished id={}", id);
            return dto;
        });
    }

    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectResponseDTO create(ProjectRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=create.started name={}", dto.name());

            String tenantId = TenantContext.getCurrentTenantId();
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            var user = (br.com.setecolinas.kanban_project.model.User) auth.getPrincipal();

            Project p = new Project(dto.name());
            p.setTenantId(tenantId);
            p.setOrganization(user.getOrganization());
            ProjectMapper.apply(p, dto);

            if (dto.responsibleIds() != null) {
                dto.responsibleIds().forEach(id ->
                        respRepo.findByIdAndTenantId(id, tenantId).ifPresent(p.getResponsibles()::add));
            }

            recalc(p);
            Project saved = repo.save(p);

            log.info("action=create.finished id={}", saved.getId());
            return ProjectMapper.toResponse(saved);
        });
    }

    @Cacheable("projects")
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> findAll(Pageable pageable) {
        return withUserContext(() -> {
            String tenantId = TenantContext.getCurrentTenantId();
            log.info("action=findAll.started tenantId={}", tenantId);

            Page<Project> page = repo.findByTenantId(tenantId, pageable);
            Page<ProjectResponseDTO> result = page.map(ProjectMapper::toResponse);

            log.info("action=findAll.finished count={}", result.getTotalElements());
            return result;
        });
    }

    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectResponseDTO update(Long id, ProjectRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=update.started id={}", id);
            String tenantId = TenantContext.getCurrentTenantId();
            Project p = repo.findByIdAndTenantId(id, tenantId)
                    .orElseThrow(() -> new NotFoundException("Project not found"));

            ProjectMapper.apply(p, dto);

            if (dto.responsibleIds() != null) {
                p.getResponsibles().clear();
                dto.responsibleIds().forEach(rid ->
                        respRepo.findByIdAndTenantId(rid, tenantId).ifPresent(p.getResponsibles()::add));
            }

            recalc(p);
            Project saved = repo.save(p);

            log.info("action=update.finished id={}", id);
            return ProjectMapper.toResponse(saved);
        });
    }

    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void delete(Long id) {
        withUserContext(() -> {
            log.info("action=delete.started id={}", id);
            String tenantId = TenantContext.getCurrentTenantId();
            Project p = repo.findByIdAndTenantId(id, tenantId)
                    .orElseThrow(() -> new NotFoundException("Project not found"));
            repo.delete(p);
            log.info("action=delete.finished id={}", id);
        });
    }

    // ====== Lógica de negócio ======
    public void recalc(Project p) {
        LocalDate today = LocalDate.now();
        p.setDaysDelay(calculateDaysDelay(p, today));
        p.setPercentTimeRemaining(calculatePercentRemaining(p, today));
        p.setStatus(calculateStatus(p, today));
    }

    private int calculateDaysDelay(Project p, LocalDate today) {
        if (p.getStatus() == ProjectStatus.CONCLUIDO) return 0;
        if (p.getPlannedEnd() == null) return 0;
        if (p.getActualEnd() != null) return 0;
        if (p.getPlannedEnd().isBefore(today)) {
            return (int) ChronoUnit.DAYS.between(p.getPlannedEnd(), today);
        }
        return 0;
    }

    private double calculatePercentRemaining(Project p, LocalDate today) {
        if (p.getStatus() == ProjectStatus.A_INICIAR) return 0.0;
        if (p.getPlannedStart() == null || p.getPlannedEnd() == null) return 0.0;

        long total = ChronoUnit.DAYS.between(p.getPlannedStart(), p.getPlannedEnd());
        if (total <= 0) return 0.0;

        long used = ChronoUnit.DAYS.between(p.getPlannedStart(), today);
        long remaining = total - used;
        if (remaining <= 0) return 0.0;

        return ((double) remaining / total) * 100.0;
    }

    private ProjectStatus calculateStatus(Project p, LocalDate today) {
        if (p.getActualEnd() != null) return ProjectStatus.CONCLUIDO;

        if (p.getActualStart() == null && p.getPlannedStart() == null && p.getPlannedEnd() == null) {
            return ProjectStatus.A_INICIAR;
        }

        if ((p.getPlannedStart() != null && p.getPlannedStart().isBefore(today) && p.getActualStart() == null)
                || (p.getPlannedEnd() != null && p.getPlannedEnd().isBefore(today) && p.getActualEnd() == null)) {
            return ProjectStatus.ATRASADO;
        }

        if (p.getActualStart() != null && (p.getPlannedEnd() == null || p.getPlannedEnd().isAfter(today)) && p.getActualEnd() == null) {
            return ProjectStatus.EM_ANDAMENTO;
        }

        return ProjectStatus.A_INICIAR;
    }

    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectResponseDTO transition(Long id, ProjectStatus target) {
        return withUserContext(() -> {
            log.info("action=transition.started id={} target={}", id, target);
            String tenantId = TenantContext.getCurrentTenantId();
            Project p = repo.findByIdAndTenantId(id, tenantId)
                    .orElseThrow(() -> new NotFoundException("Project not found"));

            LocalDate today = LocalDate.now();
            ProjectStatus current = recalcAndReturn(p);

            if (current == ProjectStatus.A_INICIAR && target == ProjectStatus.EM_ANDAMENTO) {
                p.setActualStart(today);
            } else if (current == ProjectStatus.A_INICIAR && target == ProjectStatus.ATRASADO) {
                if (today.isBefore(p.getPlannedStart())) {
                    throw new BusinessException("Cannot mark as delayed before planned start");
                }
            } else if (target == ProjectStatus.CONCLUIDO) {
                p.setActualEnd(today);
            } else if (current == ProjectStatus.EM_ANDAMENTO && target == ProjectStatus.A_INICIAR) {
                p.setActualStart(null);
            } else {
                throw new BusinessException("Transition not allowed or requires manual date adjustments");
            }

            recalc(p);
            Project saved = repo.save(p);

            log.info("action=transition.finished id={} newStatus={}", id, saved.getStatus());
            return ProjectMapper.toResponse(saved);
        });
    }

    private ProjectStatus recalcAndReturn(Project p) {
        recalc(p);
        return p.getStatus();
    }
}
