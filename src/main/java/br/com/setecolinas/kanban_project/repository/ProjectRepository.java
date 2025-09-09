package br.com.setecolinas.kanban_project.repository;

import br.com.setecolinas.kanban_project.model.Project;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(ProjectStatus status);
}
