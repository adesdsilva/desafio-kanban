package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByTenantId(String tenantId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndUserId(String tenantId, Long userId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndAction(String tenantId, String action, Pageable pageable);

    Page<AuditLog> findByTenantIdAndTimestampBetween(String tenantId, Instant start, Instant end, Pageable pageable);
}

