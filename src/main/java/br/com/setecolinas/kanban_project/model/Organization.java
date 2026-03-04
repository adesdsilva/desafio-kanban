package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "organizations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "tenant_id"),
                @UniqueConstraint(columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_org_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_org_slug", columnList = "slug")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, unique = true)
    private String tenantId;

    private String logo;

    private String website;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> users = new HashSet<>();

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Subscription subscription = null;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        if (tenantId == null) {
            tenantId = UUID.randomUUID().toString();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

