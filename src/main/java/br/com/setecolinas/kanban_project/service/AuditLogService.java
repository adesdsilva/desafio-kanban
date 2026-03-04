package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.model.AuditLog;
import br.com.setecolinas.kanban_project.model.User;
import br.com.setecolinas.kanban_project.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String tenantId, User user, String action, String entityType, Long entityId, String description) {
        try {
            String ipAddress = getClientIpAddress();

            AuditLog auditLog = AuditLog.builder()
                    .tenantId(tenantId)
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(ipAddress)
                    .timestamp(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Auditoria registrada: {} - {} - {}", tenantId, action, entityType);
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria", e);
            // Não falhar a operação por causa de auditoria
        }
    }

    public void log(String tenantId, String action, String entityType, Long entityId, String description) {
        log(tenantId, null, action, entityType, entityId, description);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByTenantId(String tenantId, Pageable pageable) {
        return auditLogRepository.findByTenantId(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByTenantIdAndUserId(String tenantId, Long userId, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndUserId(tenantId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByTenantIdAndAction(String tenantId, String action, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndAction(tenantId, action, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByTenantIdAndTimestampBetween(String tenantId, Instant start, Instant end, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndTimestampBetween(tenantId, start, end, pageable);
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String clientIp = attributes.getRequest().getHeader("X-Forwarded-For");
                if (clientIp == null || clientIp.isEmpty()) {
                    clientIp = attributes.getRequest().getRemoteAddr();
                }
                return clientIp;
            }
        } catch (Exception e) {
            log.debug("Erro ao obter IP do cliente", e);
        }
        return "UNKNOWN";
    }
}

