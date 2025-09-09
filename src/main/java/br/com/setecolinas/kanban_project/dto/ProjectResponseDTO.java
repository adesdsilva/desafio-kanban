package br.com.setecolinas.kanban_project.dto;

import java.time.LocalDate;
import java.util.Set;

public record ProjectResponseDTO(
        Long id,
        String name,
        String status,
        LocalDate plannedStart,
        LocalDate plannedEnd,
        LocalDate actualStart,
        LocalDate actualEnd,
        Integer daysDelay,
        Double percentTimeRemaining,
        Set<Long> responsibleIds
) {
}
