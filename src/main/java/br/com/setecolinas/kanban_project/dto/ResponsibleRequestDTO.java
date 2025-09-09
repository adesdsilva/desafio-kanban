package br.com.setecolinas.kanban_project.dto;

public record ResponsibleRequestDTO(
        String name, String email, String role, Long secId
) {
}
