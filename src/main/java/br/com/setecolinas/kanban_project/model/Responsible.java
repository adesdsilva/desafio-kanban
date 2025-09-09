package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "responsible",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Responsible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
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

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { name = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { email = e; }
    public String getRole() { return role; }
    public void setRole(String r) { role = r; }
    public Set<Project> getProjects() { return projects; }
    public Secretaria getSecretaria() { return secretaria; }
    public void setSecretaria(Secretaria secretaria) { this.secretaria = secretaria; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Responsible that = (Responsible) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(role, that.role) && Objects.equals(projects, that.projects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, role, projects);
    }

    @Override
    public String toString() {
        return "Responsible{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", projects=" + projects +
                '}';
    }
}
