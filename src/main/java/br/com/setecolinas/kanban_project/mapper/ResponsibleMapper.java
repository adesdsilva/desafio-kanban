package br.com.setecolinas.kanban_project.mapper;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.model.Responsible;

public final class ResponsibleMapper {
    private ResponsibleMapper() {}

    public static Responsible toEntity(ResponsibleRequestDTO dto) {
        return new Responsible(dto.name(), dto.email(), dto.role());
    }

    public static ResponsibleResponseDTO toResponse(Responsible r) {
        Long secId = r.getSecretaria() == null ? null : r.getSecretaria().getId();
        return new ResponsibleResponseDTO(r.getId(), r.getName(), r.getEmail(), r.getRole(), secId);
    }
}
