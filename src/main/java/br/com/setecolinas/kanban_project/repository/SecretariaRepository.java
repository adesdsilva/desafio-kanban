package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Secretaria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {
    // Multi-tenant methods
    Optional<Secretaria> findByIdAndTenantId(Long id, String tenantId);
    Page<Secretaria> findByTenantId(String tenantId, Pageable pageable);
}
