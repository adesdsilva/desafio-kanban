package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible, Long> {
    Optional<Responsible> findByEmail(String email);
    boolean existsByEmail(String email);
}
