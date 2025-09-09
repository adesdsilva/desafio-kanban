package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "responsible",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"),
        indexes = {
                @Index(name = "idx_responsible_email", columnList = "email"),
                @Index(name = "idx_responsible_name", columnList = "name")
        }
)
public class Responsible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    @ManyToMany(mappedBy = "responsibles")
    private Set<Project> projects = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "secretaria_id")
    private Secretaria secretaria;

    public Responsible() {}

    public Responsible(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Set<Project> getProjects() { return projects; }
    public void setProjects(Set<Project> projects) { this.projects = projects; }
    public Secretaria getSecretaria() { return secretaria; }
    public void setSecretaria(Secretaria secretaria) { this.secretaria = secretaria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Responsible)) return false;
        Responsible that = (Responsible) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
