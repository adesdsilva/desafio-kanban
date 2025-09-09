package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.service.SecretariaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/secretarias")
public class SecretariaController {

    private final SecretariaService service;

    public SecretariaController(SecretariaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SecretariaResponseDTO> create(
            @Valid @RequestBody SecretariaRequestDTO dto) {
        var out = service.create(dto);
        return ResponseEntity.created(URI.create("/api/secretarias/" + out.id())).body(out);
    }

    @GetMapping
    public ResponseEntity<Page<SecretariaResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretariaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecretariaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SecretariaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
