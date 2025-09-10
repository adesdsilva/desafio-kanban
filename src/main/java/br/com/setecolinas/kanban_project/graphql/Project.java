package br.com.setecolinas.kanban_project.graphql;

public record Project(
        String id,
        String name,
        String status,
        String plannedStart,
        String plannedEnd,
        String actualStart,
        String actualEnd
) {}

