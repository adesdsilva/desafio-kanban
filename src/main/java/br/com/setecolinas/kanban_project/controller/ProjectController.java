package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import br.com.setecolinas.kanban_project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService s){ this.service = s; }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@Valid @RequestBody ProjectRequestDTO dto){
        var out = service.create(dto);
        return ResponseEntity.created(URI.create("/api/projects/" + out.id())).body(out);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> findAll(){ return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> findById(@PathVariable Long id){ return ResponseEntity.ok(service.update(id, null)); }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PostMapping("/{id}/transition")
    public ResponseEntity<ProjectResponseDTO> transition(@PathVariable Long id, @RequestParam("target") ProjectStatus target){
        return ResponseEntity.ok(service.transition(id, target));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
