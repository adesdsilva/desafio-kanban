package br.com.setecolinas.kanban_project.model.enums;

public enum UserRole {
    ADMIN("Administrador do Sistema"),
    ORG_ADMIN("Administrador da Organização"),
    MEMBER("Membro da Equipe");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

