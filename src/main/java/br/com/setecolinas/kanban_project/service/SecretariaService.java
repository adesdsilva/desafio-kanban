package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.model.Secretaria;
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
public class SecretariaService {

    private static final Logger log = LoggerFactory.getLogger(SecretariaService.class);

    private final SecretariaRepository repo;

    public SecretariaService(SecretariaRepository repo) {
        this.repo = repo;
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
    public SecretariaResponseDTO findById(Long id) {
        return withUserContext(() -> {
            log.info("action=findById.started id={}", id);
            Secretaria s = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Secretaria not found"));
            log.info("action=findById.finished id={}", id);
            return new SecretariaResponseDTO(s.getId(), s.getNome(), s.getDescricao());
        });
    }

    @Transactional
    @CacheEvict(value = "secretarias", allEntries = true)
    public SecretariaResponseDTO create(SecretariaRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=create.started nome={}", dto.nome());
            Secretaria s = new Secretaria(dto.nome(), dto.descricao());
            Secretaria saved = repo.save(s);
            log.info("action=create.finished id={}", saved.getId());
            return new SecretariaResponseDTO(saved.getId(), saved.getNome(), saved.getDescricao());
        });
    }

    @Cacheable("secretarias")
    @Transactional(readOnly = true)
    public Page<SecretariaResponseDTO> findAll(Pageable pageable) {
        return withUserContext(() -> {
            log.info("action=findAll.started page={} size={}", pageable.getPageNumber(), pageable.getPageSize());

            Page<SecretariaResponseDTO> out = repo.findAll(pageable)
                    .map(s -> new SecretariaResponseDTO(s.getId(), s.getNome(), s.getDescricao()));

            log.info("action=findAll.finished totalElements={} totalPages={}",
                    out.getTotalElements(), out.getTotalPages());

            return out;
        });
    }

    @Transactional
    @CacheEvict(value = "secretarias", allEntries = true)
    public SecretariaResponseDTO update(Long id, SecretariaRequestDTO dto) {
        return withUserContext(() -> {
            log.info("action=update.started id={}", id);
            Secretaria s = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Secretaria not found"));
            s.setNome(dto.nome());
            s.setDescricao(dto.descricao());
            Secretaria saved = repo.save(s);
            log.info("action=update.finished id={}", id);
            return new SecretariaResponseDTO(saved.getId(), saved.getNome(), saved.getDescricao());
        });
    }

    @Transactional
    @CacheEvict(value = "secretarias", allEntries = true)
    public void delete(Long id) {
        withUserContext(() -> {
            log.info("action=delete.started id={}", id);
            if (!repo.existsById(id)) {
                throw new NotFoundException("Secretaria not found");
            }
            repo.deleteById(id);
            log.info("action=delete.finished id={}", id);
        });
    }
}
