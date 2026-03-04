package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenantId(String email, String tenantId);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.tenantId = ?1")
    Page<User> findByTenantId(String tenantId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.organization.id = ?1 AND u.tenantId = ?2")
    Page<User> findByOrganizationIdAndTenantId(Long organizationId, String tenantId, Pageable pageable);

    long countByTenantId(String tenantId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndTenantId(String email, String tenantId);
}

