package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.BusinessException;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.mapper.ProjectMapper;
import br.com.setecolinas.kanban_project.model.Project;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import br.com.setecolinas.kanban_project.repository.ProjectRepository;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository repo;
    private final ResponsibleRepository respRepo;

    public ProjectService(ProjectRepository repo, ResponsibleRepository respRepo){
        this.repo = repo; this.respRepo = respRepo;
    }

    @Transactional
    public ProjectResponseDTO create(ProjectRequestDTO dto){
        log.info("[ProjectService] START create - name={}", dto.name());
        Project p = new Project(dto.name());
        ProjectMapper.apply(p, dto);
        // set responsibles if provided
        if (dto.responsibleIds() != null) {
            dto.responsibleIds().forEach(id -> respRepo.findById(id).ifPresent(p.getResponsibles()::add));
        }
        recalc(p);
        Project saved = repo.save(p);
        log.info("[ProjectService] END create id={}", saved.getId());
        return ProjectMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> findAll(){
        log.info("[ProjectService] START findAll");
        var out = repo.findAll().stream().map(ProjectMapper::toResponse).collect(Collectors.toList());
        log.info("[ProjectService] END findAll count={}", out.size());
        return out;
    }

    @Transactional
    public ProjectResponseDTO update(Long id, ProjectRequestDTO dto){
        log.info("[ProjectService] START update id={}", id);
        Project p = repo.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
        ProjectMapper.apply(p, dto);
        // update responsibles like create
        recalc(p);
        Project saved = repo.save(p);
        log.info("[ProjectService] END update id={}", id);
        return ProjectMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id){
        log.info("[ProjectService] START delete id={}", id);
        if (!repo.existsById(id)) throw new NotFoundException("Project not found");
        repo.deleteById(id);
        log.info("[ProjectService] END delete id={}", id);
    }

    // central: recalc metrics and status (applies business rules)
    public void recalc(Project p){
        // compute daysDelay
        LocalDate today = LocalDate.now();
        // percentTimeRemaining
        p.setDaysDelay(calculateDaysDelay(p, today));
        p.setPercentTimeRemaining(calculatePercentRemaining(p, today));
        p.setStatus(calculateStatus(p, today));
    }

    private int calculateDaysDelay(Project p, LocalDate today){
        if (p.getStatus() == ProjectStatus.CONCLUIDO) return 0;
        if (p.getPlannedEnd() == null) return 0;
        if (p.getActualEnd() != null) return 0;
        if (p.getPlannedEnd().isBefore(today)) {
            return (int) ChronoUnit.DAYS.between(p.getPlannedEnd(), today);
        }
        return 0;
    }

    private double calculatePercentRemaining(Project p, LocalDate today){
        if (p.getStatus() == ProjectStatus.A_INICIAR) return 0.0;
        if (p.getPlannedStart() == null || p.getPlannedEnd() == null) return 0.0;
        long total = ChronoUnit.DAYS.between(p.getPlannedStart(), p.getPlannedEnd());
        if (total <= 0) return 0.0;
        long used = ChronoUnit.DAYS.between(p.getPlannedStart(), today);
        long remaining = total - used;
        if (remaining <= 0) return 0.0;
        return ((double) remaining / (double) total) * 100.0;
    }

    private ProjectStatus calculateStatus(Project p, LocalDate today){
        // Use the rules in the spec
        if (p.getActualEnd() != null) return ProjectStatus.CONCLUIDO;

        if (p.getActualStart() == null && p.getPlannedStart() == null && p.getPlannedEnd() == null) {
            return ProjectStatus.A_INICIAR;
        }

        if ((p.getPlannedStart() != null && p.getPlannedStart().isBefore(today)
                && p.getActualStart() == null)
                || (p.getPlannedEnd() != null && p.getPlannedEnd().isBefore(today)
                && p.getActualEnd() == null && p.getActualEnd()==null)) {
            return ProjectStatus.ATRASADO;
        }

        if (p.getActualStart() != null && (p.getPlannedEnd() == null || p.getPlannedEnd().isAfter(today)) && p.getActualEnd() == null) {
            return ProjectStatus.EM_ANDAMENTO;
        }

        return ProjectStatus.A_INICIAR;
    }

    // Add transition API that applies requested transition and validates (table rules)
    @Transactional
    public ProjectResponseDTO transition(Long id, ProjectStatus target) {
        log.info("[ProjectService] START transition id={} to {}", id, target);
        Project p = repo.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
        // implement table of transitions: for demo we apply minimal checks:
        // Example: A_INICIAR -> EM_ANDAMENTO => set actualStart = today
        LocalDate today = LocalDate.now();
        ProjectStatus current = recalcAndReturn(p);
        if (current == ProjectStatus.A_INICIAR && target == ProjectStatus.EM_ANDAMENTO){
            p.setActualStart(today);
        } else if (current == ProjectStatus.A_INICIAR && target == ProjectStatus.ATRASADO){
            if (today.isBefore(p.getPlannedStart())) throw new BusinessException("Cannot mark as delayed before planned start");
            // else we can set nothing, recalc will mark as ATRASADO
        } else if (target == ProjectStatus.CONCLUIDO){
            p.setActualEnd(today);
        } else if (current == ProjectStatus.EM_ANDAMENTO && target == ProjectStatus.A_INICIAR){
            p.setActualStart(null);
        } else {
            throw new BusinessException("Transition not allowed or requires manual date adjustments");
        }
        recalc(p);
        Project saved = repo.save(p);
        log.info("[ProjectService] END transition id={} newStatus={}", id, saved.getStatus());
        return ProjectMapper.toResponse(saved);
    }

    private ProjectStatus recalcAndReturn(Project p){
        recalc(p);
        return p.getStatus();
    }
}
