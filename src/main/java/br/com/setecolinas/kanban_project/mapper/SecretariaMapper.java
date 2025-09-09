package br.com.setecolinas.kanban_project.mapper;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.model.Secretaria;

public final class SecretariaMapper {

    private SecretariaMapper() {}

    public static Secretaria toEntity(SecretariaRequestDTO dto) {
        return new Secretaria(dto.nome(), dto.descricao());
    }

    public static SecretariaResponseDTO toResponse(Secretaria s) {
        return new SecretariaResponseDTO(s.getId(), s.getNome(), s.getDescricao());
    }
}
