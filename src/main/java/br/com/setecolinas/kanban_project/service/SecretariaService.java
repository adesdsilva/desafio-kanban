package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.mapper.SecretariaMapper;
import br.com.setecolinas.kanban_project.model.Secretaria;
import br.com.setecolinas.kanban_project.repository.SecretariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecretariaService {
    private static final Logger log = LoggerFactory.getLogger(SecretariaService.class);
    private final SecretariaRepository repo;

    public SecretariaService(SecretariaRepository repo) { this.repo = repo; }

    @CacheEvict(value = {"secretarias", "secretariasPage"}, allEntries = true)
    @Transactional
    public SecretariaResponseDTO create(SecretariaRequestDTO dto) {
        log.info("[SecretariaService] START create nome={}", dto.nome());
        Secretaria s = SecretariaMapper.toEntity(dto);
        Secretaria saved = repo.save(s);
        log.info("[SecretariaService] END create id={}", saved.getId());
        return SecretariaMapper.toResponse(saved);
    }

    @Cacheable(value = "secretariasPage", key = "{#pageable.pageNumber, #pageable.pageSize}")
    @Transactional(readOnly = true)
    public Page<SecretariaResponseDTO> findAll(Pageable pageable) {
        log.info("[SecretariaService] START findAll pageable={}", pageable);
        var page = repo.findAll(pageable).map(SecretariaMapper::toResponse);
        log.info("[SecretariaService] END findAll size={}", page.getSize());
        return page;
    }

    @Cacheable(value = "secretarias", key = "#id")
    @Transactional(readOnly = true)
    public SecretariaResponseDTO findById(Long id) {
        log.info("[SecretariaService] START findById id={}", id);
        Secretaria s = repo.findById(id).orElseThrow(() -> new NotFoundException("Secretaria não encontrada"));
        log.info("[SecretariaService] END findById id={}", id);
        return SecretariaMapper.toResponse(s);
    }

    @CacheEvict(value = {"secretarias", "secretariasPage"}, allEntries = true)
    @Transactional
    public SecretariaResponseDTO update(Long id, SecretariaRequestDTO dto) {
        log.info("[SecretariaService] START update id={}", id);
        Secretaria s = repo.findById(id).orElseThrow(() -> new NotFoundException("Secretaria não encontrada"));
        s.setNome(dto.nome());
        s.setDescricao(dto.descricao());
        Secretaria saved = repo.save(s);
        log.info("[SecretariaService] END update id={}", id);
        return SecretariaMapper.toResponse(saved);
    }

    @CacheEvict(value = {"secretarias", "secretariasPage"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        log.info("[SecretariaService] START delete id={}", id);
        if (!repo.existsById(id)) throw new NotFoundException("Secretaria não encontrada");
        repo.deleteById(id);
        log.info("[SecretariaService] END delete id={}", id);
    }
}
