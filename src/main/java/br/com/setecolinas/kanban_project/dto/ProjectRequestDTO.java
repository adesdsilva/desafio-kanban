package br.com.setecolinas.kanban_project.dto;

import java.time.LocalDate;
import java.util.Set;

public record ProjectRequestDTO(
        String name,
        LocalDate plannedStart,
        LocalDate plannedEnd,
        LocalDate actualStart,
        LocalDate actualEnd,
        Set<Long> responsibleIds
) {
}
