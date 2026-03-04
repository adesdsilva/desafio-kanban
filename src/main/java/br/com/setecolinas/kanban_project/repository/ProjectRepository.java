package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Multi-tenant methods
    Page<Project> findByTenantId(String tenantId, Pageable pageable);
    Optional<Project> findByIdAndTenantId(Long id, String tenantId);
}
