package br.com.setecolinas.kanban_project.mapper;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.model.Project;
import br.com.setecolinas.kanban_project.model.Responsible;

import java.util.Set;
import java.util.stream.Collectors;

public final class ProjectMapper {
    private ProjectMapper(){}

    public static ProjectResponseDTO toResponse(Project p) {
        Set<Long> ids = p.getResponsibles().stream().map(Responsible::getId).collect(Collectors.toSet());
        return new ProjectResponseDTO(p.getId(), p.getName(), p.getStatus() == null ? null : p.getStatus().name(),
                p.getPlannedStart(), p.getPlannedEnd(), p.getActualStart(), p.getActualEnd(),
                p.getDaysDelay(), p.getPercentTimeRemaining(), ids);
    }

    public static void apply(Project p, ProjectRequestDTO dto) {
        if (dto.name() != null) p.setName(dto.name());
        p.setPlannedStart(dto.plannedStart());
        p.setPlannedEnd(dto.plannedEnd());
        p.setActualStart(dto.actualStart());
        p.setActualEnd(dto.actualEnd());
    }
}
