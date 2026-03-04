package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Responsible;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible, Long> {
    // Multi-tenant methods
    Page<Responsible> findByTenantId(String tenantId, Pageable pageable);
    Optional<Responsible> findByIdAndTenantId(Long id, String tenantId);
    Page<Responsible> findByTenantIdAndNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String tenantId, String name, String email, Pageable pageable);
}
