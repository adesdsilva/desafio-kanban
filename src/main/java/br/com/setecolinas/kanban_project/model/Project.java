package br.com.setecolinas.kanban_project.model;


import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    // dates
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDate actualStart;
    private LocalDate actualEnd;

    private Integer daysDelay;
    private Double percentTimeRemaining;

    // audit
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(name = "project_responsible",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns = @JoinColumn(name="responsible_id"))
    private Set<Responsible> responsibles = new HashSet<>();

    public Project() {}

    public Project(String name) { this.name = name; }

    // getters/setters ...
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { name = n; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus s) { status = s; }
    public LocalDate getPlannedStart() { return plannedStart; }
    public void setPlannedStart(LocalDate d) { plannedStart = d; }
    public LocalDate getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(LocalDate d) { plannedEnd = d; }
    public LocalDate getActualStart() { return actualStart; }
    public void setActualStart(LocalDate d) { actualStart = d; }
    public LocalDate getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDate d) { actualEnd = d; }
    public Integer getDaysDelay() { return daysDelay; }
    public void setDaysDelay(Integer v) { daysDelay = v; }
    public Double getPercentTimeRemaining() { return percentTimeRemaining; }
    public void setPercentTimeRemaining(Double p) { percentTimeRemaining = p; }
    public Set<Responsible> getResponsibles(){ return responsibles; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); updatedAt = Instant.now(); }
    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && status == project.status && Objects.equals(plannedStart, project.plannedStart) && Objects.equals(plannedEnd, project.plannedEnd) && Objects.equals(actualStart, project.actualStart) && Objects.equals(actualEnd, project.actualEnd) && Objects.equals(daysDelay, project.daysDelay) && Objects.equals(percentTimeRemaining, project.percentTimeRemaining) && Objects.equals(createdAt, project.createdAt) && Objects.equals(updatedAt, project.updatedAt) && Objects.equals(responsibles, project.responsibles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, plannedStart, plannedEnd, actualStart, actualEnd, daysDelay, percentTimeRemaining, createdAt, updatedAt, responsibles);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", plannedStart=" + plannedStart +
                ", plannedEnd=" + plannedEnd +
                ", actualStart=" + actualStart +
                ", actualEnd=" + actualEnd +
                ", daysDelay=" + daysDelay +
                ", percentTimeRemaining=" + percentTimeRemaining +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", responsibles=" + responsibles +
                '}';
    }
}

