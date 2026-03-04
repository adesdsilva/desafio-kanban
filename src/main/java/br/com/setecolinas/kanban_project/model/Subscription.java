package br.com.setecolinas.kanban_project.model;

import br.com.setecolinas.kanban_project.model.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, unique = true)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlanType plan = PlanType.FREE;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(updatable = false)
    @Builder.Default
    private Instant startDate = Instant.now();

    private Instant endDate;

    private String stripeCustomerId;

    private String stripeSubscriptionId;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (startDate == null) {
            startDate = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isActive() {
        return endDate == null || endDate.isAfter(Instant.now());
    }

    public boolean isFree() {
        return plan == PlanType.FREE;
    }

    public boolean isPro() {
        return plan == PlanType.PRO;
    }
}

