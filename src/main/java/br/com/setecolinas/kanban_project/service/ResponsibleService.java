package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.model.Responsible;
import br.com.setecolinas.kanban_project.model.Secretaria;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import br.com.setecolinas.kanban_project.repository.SecretariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ResponsibleService {

    private static final Logger log = LoggerFactory.getLogger(ResponsibleService.class);

    private final ResponsibleRepository repo;
    private final SecretariaRepository secRepo;

    public ResponsibleService(ResponsibleRepository repo, SecretariaRepository secRepo) {
        this.repo = repo;
        this.secRepo = secRepo;
    }

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
        Object principal = auth.getPrincipal();
        if (principal instanceof String) return (String) principal;
        return auth.getName();
    }

    @Transactional(readOnly = true)
    public ResponsibleResponseDTO findById(Long id) {
        return withUserContext(() -> {
            log.info("action=findById.started id={}", id);
            Responsible r = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Responsible not found"));
            Long secId = r.getSecretaria() != null ? r.getSecretaria().getId() : null;
            ResponsibleResponseDTO dto = new ResponsibleResponseDTO(r.getId(), r.getName(), r.getEmail(), r.getRole(), secId);
            log.info("action=findById.finished id={}", id);
            return dto;
        });
    }

    @Transactional
    @CacheEvict(value = "responsibles", allEntries = true)
    public ResponsibleResponseDTO create(ResponsibleRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=create.started name={}", dto.name());

            Responsible r = new Responsible(dto.name(), dto.email(), dto.role());

            if (dto.secId() != null) {
                Secretaria s = secRepo.findById(dto.secId())
                        .orElseThrow(() -> new NotFoundException("Secretaria not found"));
                r.setSecretaria(s);
            }

            Responsible saved = repo.save(r);
            Long secId = saved.getSecretaria() != null ? saved.getSecretaria().getId() : null;
            log.info("action=create.finished id={}", saved.getId());
            return new ResponsibleResponseDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), secId);
        });
    }

    @Cacheable(value = "responsiblesPage", key = "{#search, #pageable.pageNumber, #pageable.pageSize}")
    @Transactional(readOnly = true)
    public Page<ResponsibleResponseDTO> findAll(String search, Pageable pageable) {
        return withUserContext(() -> {
            log.info("action=findAll.started search={} page={} size={}", search, pageable.getPageNumber(), pageable.getPageSize());

            Page<Responsible> pageResult = (search == null || search.isBlank())
                    ? repo.findAll(pageable)
                    : repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);

            Page<ResponsibleResponseDTO> out = pageResult.map(r ->
                    new ResponsibleResponseDTO(r.getId(), r.getName(), r.getEmail(), r.getRole(),
                            r.getSecretaria() != null ? r.getSecretaria().getId() : null));

            log.info("action=findAll.finished totalElements={} totalPages={}",
                    out.getTotalElements(), out.getTotalPages());

            return out;
        });
    }


    @Transactional
    @CacheEvict(value = "responsibles", allEntries = true)
    public ResponsibleResponseDTO update(Long id, ResponsibleRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=update.started id={}", id);
            Responsible r = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Responsible not found"));

            r.setName(dto.name());
            r.setEmail(dto.email());
            r.setRole(dto.role());

            if (dto.secId() != null) {
                Secretaria s = secRepo.findById(dto.secId())
                        .orElseThrow(() -> new NotFoundException("Secretaria not found"));
                r.setSecretaria(s);
            } else {
                r.setSecretaria(null);
            }

            Responsible saved = repo.save(r);
            Long secId = saved.getSecretaria() != null ? saved.getSecretaria().getId() : null;
            log.info("action=update.finished id={}", id);
            return new ResponsibleResponseDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), secId);
        });
    }

    @Transactional
    @CacheEvict(value = "responsibles", allEntries = true)
    public void delete(Long id) {
        withUserContext(() -> {
            log.info("action=delete.started id={}", id);
            if (!repo.existsById(id)) {
                throw new NotFoundException("Responsible not found");
            }
            repo.deleteById(id);
            log.info("action=delete.finished id={}", id);
        });
    }
}
