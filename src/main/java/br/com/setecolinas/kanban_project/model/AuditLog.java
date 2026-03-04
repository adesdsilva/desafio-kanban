package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_tenant", columnList = "tenant_id"),
                @Index(name = "idx_audit_user", columnList = "user_id"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String ipAddress;

    @Column(updatable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}

