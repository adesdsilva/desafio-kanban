package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "project",
        indexes = {
                @Index(name = "idx_project_name", columnList = "name")
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    // datas
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDate actualStart;
    private LocalDate actualEnd;

    private Integer daysDelay;
    private Double percentTimeRemaining;

    // auditoria
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(
            name = "project_responsible",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "responsible_id"),
            indexes = {
                    @Index(name = "idx_project_responsible_proj", columnList = "project_id"),
                    @Index(name = "idx_project_responsible_resp", columnList = "responsible_id")
            }
    )
    private Set<Responsible> responsibles = new HashSet<>();

    public Project() {}
    public Project(String name) { this.name = name; }

    // Getters e Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public LocalDate getPlannedStart() { return plannedStart; }
    public void setPlannedStart(LocalDate plannedStart) { this.plannedStart = plannedStart; }
    public LocalDate getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(LocalDate plannedEnd) { this.plannedEnd = plannedEnd; }
    public LocalDate getActualStart() { return actualStart; }
    public void setActualStart(LocalDate actualStart) { this.actualStart = actualStart; }
    public LocalDate getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDate actualEnd) { this.actualEnd = actualEnd; }
    public Integer getDaysDelay() { return daysDelay; }
    public void setDaysDelay(Integer daysDelay) { this.daysDelay = daysDelay; }
    public Double getPercentTimeRemaining() { return percentTimeRemaining; }
    public void setPercentTimeRemaining(Double percentTimeRemaining) { this.percentTimeRemaining = percentTimeRemaining; }
    public Set<Responsible> getResponsibles() { return responsibles; }
    public void setResponsibles(Set<Responsible> responsibles) { this.responsibles = responsibles; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); updatedAt = Instant.now(); }
    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
