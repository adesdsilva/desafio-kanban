package br.com.setecolinas.kanban_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String password,

        @NotBlank(message = "Nome da organização é obrigatório")
        @Size(min = 3, max = 100, message = "Nome da organização deve ter entre 3 e 100 caracteres")
        String organizationName,

        @NotBlank(message = "Slug da organização é obrigatório")
        @Size(min = 3, max = 50, message = "Slug deve ter entre 3 e 50 caracteres")
        String organizationSlug
) {
}

