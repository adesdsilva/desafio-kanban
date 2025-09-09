package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.BusinessException;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.mapper.ResponsibleMapper;
import br.com.setecolinas.kanban_project.model.Responsible;
import br.com.setecolinas.kanban_project.model.Secretaria;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import br.com.setecolinas.kanban_project.repository.SecretariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsibleService {
    private static final Logger log = LoggerFactory.getLogger(ResponsibleService.class);
    private final ResponsibleRepository repo;
    private final SecretariaRepository secretariaRepo;

    public ResponsibleService(ResponsibleRepository repo, SecretariaRepository secretariaRepo) {
        this.repo = repo;
        this.secretariaRepo = secretariaRepo;
    }

    @Transactional
    public ResponsibleResponseDTO create(ResponsibleRequestDTO dto) {
        log.info("[ResponsibleService] START create email={}", dto.email());
        if (repo.existsByEmail(dto.email())) throw new BusinessException("Email já cadastrado");
        Responsible r = ResponsibleMapper.toEntity(dto);
        if (dto.secId() != null) {
            Secretaria s = secretariaRepo.findById(dto.secId())
                    .orElseThrow(() -> new NotFoundException("Secretaria não encontrada"));
            r.setSecretaria(s);
        }
        Responsible saved = repo.save(r);
        log.info("[ResponsibleService] END create id={}", saved.getId());
        return ResponsibleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResponsibleResponseDTO> findAll() {
        log.info("[ResponsibleService] START findAll");
        var out = repo.findAll().stream().map(ResponsibleMapper::toResponse).collect(Collectors.toList());
        log.info("[ResponsibleService] END findAll count={}", out.size());
        return out;
    }

    @Transactional(readOnly = true)
    public ResponsibleResponseDTO findById(Long id) {
        log.info("[ResponsibleService] START findById id={}", id);
        Responsible r = repo.findById(id).orElseThrow(() -> new NotFoundException("Responsável não encontrado"));
        log.info("[ResponsibleService] END findById id={}", id);
        return ResponsibleMapper.toResponse(r);
    }

    @Transactional
    public ResponsibleResponseDTO update(Long id, ResponsibleRequestDTO dto) {
        log.info("[ResponsibleService] START update id={}", id);
        Responsible r = repo.findById(id).orElseThrow(() -> new NotFoundException("Responsável não encontrado"));
        if (!r.getEmail().equals(dto.email()) && repo.existsByEmail(dto.email())) throw new BusinessException("Email já cadastrado");
        r.setName(dto.name());
        r.setEmail(dto.email());
        r.setRole(dto.role());
        if (dto.secId() != null) {
            Secretaria s = secretariaRepo.findById(dto.secId())
                    .orElseThrow(() -> new NotFoundException("Secretaria não encontrada"));
            r.setSecretaria(s);
        } else {
            r.setSecretaria(null);
        }
        Responsible saved = repo.save(r);
        log.info("[ResponsibleService] END update id={}", id);
        return ResponsibleMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        log.info("[ResponsibleService] START delete id={}", id);
        if (!repo.existsById(id)) throw new NotFoundException("Responsável não encontrado");
        repo.deleteById(id);
        log.info("[ResponsibleService] END delete id={}", id);
    }
}
