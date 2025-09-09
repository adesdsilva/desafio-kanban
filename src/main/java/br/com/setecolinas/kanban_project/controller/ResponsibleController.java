package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.service.ResponsibleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsibleController {

    private final ResponsibleService service;

    public ResponsibleController(ResponsibleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResponsibleResponseDTO> create(
            @Valid @RequestBody ResponsibleRequestDTO dto) {
        var out = service.create(dto);
        return ResponseEntity.created(URI.create("/api/responsaveis/" + out.id())).body(out);
    }

    @GetMapping
    public ResponseEntity<Page<ResponsibleResponseDTO>> findAll(
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(service.findAll(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsibleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsibleResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody ResponsibleRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
