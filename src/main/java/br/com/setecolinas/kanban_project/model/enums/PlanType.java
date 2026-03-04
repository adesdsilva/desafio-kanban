package br.com.setecolinas.kanban_project.model.enums;

public enum PlanType {
    FREE("Plano Gratuito", 3, false),
    PRO("Plano Profissional", Integer.MAX_VALUE, true),
    ENTERPRISE("Plano Enterprise", Integer.MAX_VALUE, true);

    private final String description;
    private final int maxProjects;
    private final boolean advancedFeatures;

    PlanType(String description, int maxProjects, boolean advancedFeatures) {
        this.description = description;
        this.maxProjects = maxProjects;
        this.advancedFeatures = advancedFeatures;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxProjects() {
        return maxProjects;
    }

    public boolean hasAdvancedFeatures() {
        return advancedFeatures;
    }
}

