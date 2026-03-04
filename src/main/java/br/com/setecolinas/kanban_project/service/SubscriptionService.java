package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.exceptions.ResourceNotFoundException;
import br.com.setecolinas.kanban_project.model.*;
import br.com.setecolinas.kanban_project.model.enums.PlanType;
import br.com.setecolinas.kanban_project.repository.OrganizationRepository;
import br.com.setecolinas.kanban_project.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    public Subscription findByOrganizationId(Long organizationId) {
        return subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription não encontrada"));
    }

    public Subscription findByTenantId(String tenantId) {
        return subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription não encontrada"));
    }

    public void upgradeToPro(String tenantId) {
        log.info("Atualizando subscription para PRO: {}", tenantId);

        Subscription subscription = findByTenantId(tenantId);
        subscription.setPlan(PlanType.PRO);
        subscriptionRepository.save(subscription);

        auditLogService.log(tenantId, "UPGRADE_PLAN", "Subscription", subscription.getId(),
                "Plano atualizado para PRO");
        log.info("Subscription atualizada para PRO: {}", tenantId);
    }

    public void downgradeToFree(String tenantId) {
        log.info("Degradando subscription para FREE: {}", tenantId);

        Subscription subscription = findByTenantId(tenantId);
        subscription.setPlan(PlanType.FREE);
        subscriptionRepository.save(subscription);

        auditLogService.log(tenantId, "DOWNGRADE_PLAN", "Subscription", subscription.getId(),
                "Plano degradado para FREE");
        log.info("Subscription degradada para FREE: {}", tenantId);
    }

    public boolean canCreateProject(String tenantId) {
        Subscription subscription = findByTenantId(tenantId);
        PlanType plan = subscription.getPlan();

        // Implementar contagem real de projetos conforme necessário
        log.debug("Verificando permissão de criar projeto para tenantId: {} com plano: {}", tenantId, plan);

        return true;  // Simplificado por enquanto
    }

    public boolean hasAdvancedFeatures(String tenantId) {
        Subscription subscription = findByTenantId(tenantId);
        return subscription.getPlan().hasAdvancedFeatures();
    }

    public PlanType getCurrentPlan(String tenantId) {
        Subscription subscription = findByTenantId(tenantId);
        return subscription.getPlan();
    }
}

