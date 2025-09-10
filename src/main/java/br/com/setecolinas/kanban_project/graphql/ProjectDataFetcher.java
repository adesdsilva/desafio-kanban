package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.service.ProjectService;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;

@DgsComponent
public class ProjectDataFetcher {

    private final ProjectService projectService;

    public ProjectDataFetcher(ProjectService projectService) {
        this.projectService = projectService;
    }

    // --- Queries ---

    @DgsQuery
    public List<ProjectResponseDTO> projects() {
        return projectService.findAll();
    }

    @DgsQuery
    public ProjectResponseDTO project(@InputArgument Long id) {
        return projectService.findAll().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    // --- Mutations ---

    @DgsMutation
    public ProjectResponseDTO createProject(@InputArgument(name = "request") ProjectRequestDTO request) {
        return projectService.create(request);
    }

    @DgsMutation
    public ProjectResponseDTO updateProject(@InputArgument Long id,
                                            @InputArgument(name = "request") ProjectRequestDTO request) {
        return projectService.update(id, request);
    }

    @DgsMutation
    public Boolean deleteProject(@InputArgument Long id) {
        projectService.delete(id);
        return true;
    }

    @DgsMutation
    public ProjectResponseDTO transitionProject(@InputArgument Long id,
                                                @InputArgument String target) {
        ProjectStatus targetStatus;
        try {
            targetStatus = ProjectStatus.valueOf(target.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid project status: " + target);
        }
        return projectService.transition(id, targetStatus);
    }
}
