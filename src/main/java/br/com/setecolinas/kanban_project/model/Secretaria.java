package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "secretaria",
        indexes = {
                @Index(name = "idx_secretaria_nome", columnList = "nome"),
                @Index(name = "idx_secretaria_tenant", columnList = "tenant_id")
        }
)
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    // Multi-tenant
    @Column(nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "secretaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Responsible> responsaveis = new HashSet<>();

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    public Secretaria() {}

    public Secretaria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Set<Responsible> getResponsaveis() { return responsaveis; }
    public void setResponsaveis(Set<Responsible> responsaveis) { this.responsaveis = responsaveis; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Secretaria)) return false;
        Secretaria that = (Secretaria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
