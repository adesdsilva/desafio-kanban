package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import br.com.setecolinas.kanban_project.service.ProjectService;
import com.netflix.graphql.dgs.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class ProjectDataFetcher {

    private final ProjectService projectService;

    public ProjectDataFetcher(ProjectService projectService) {
        this.projectService = projectService;
    }

    // --- Queries ---

    @DgsQuery
    public Page<ProjectResponseDTO> projects(@InputArgument Integer page,
                                             @InputArgument Integer size) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;
        return projectService.findAll(PageRequest.of(p, s));
    }


    @DgsQuery
    public ProjectResponseDTO project(@InputArgument Long id) {
        return projectService.findById(id);
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
        ProjectStatus targetStatus = ProjectStatus.valueOf(target.toUpperCase());
        return projectService.transition(id, targetStatus);
    }
}
