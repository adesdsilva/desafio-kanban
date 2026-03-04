package br.com.setecolinas.kanban_project.dto;

import java.time.Instant;

public record AuthResponseDTO(
        String token,
        Long userId,
        String email,
        String name,
        String role,
        Long organizationId,
        String organizationName,
        String tenantId,
        Instant expiresAt
) {
}
