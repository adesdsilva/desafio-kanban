package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByTenantId(String tenantId);

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByTenantId(String tenantId);
}

