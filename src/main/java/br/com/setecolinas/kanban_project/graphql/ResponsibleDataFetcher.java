package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.service.ResponsibleService;
import com.netflix.graphql.dgs.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class ResponsibleDataFetcher {

    private final ResponsibleService service;

    public ResponsibleDataFetcher(ResponsibleService service) {
        this.service = service;
    }

    // --- Queries ---

    @DgsQuery
    public Page<ResponsibleResponseDTO> responsibles(@InputArgument Integer page,
                                                     @InputArgument Integer size,
                                                     @InputArgument String search) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;
        return service.findAll(search, PageRequest.of(p, s));
    }

    @DgsQuery
    public ResponsibleResponseDTO responsible(@InputArgument Long id) {
        return service.findById(id);
    }

    // --- Mutations ---

    @DgsMutation
    public ResponsibleResponseDTO createResponsible(@InputArgument(name = "request") ResponsibleRequestDTO request) {
        return service.create(request);
    }

    @DgsMutation
    public ResponsibleResponseDTO updateResponsible(@InputArgument Long id,
                                                    @InputArgument(name = "request") ResponsibleRequestDTO request) {
        return service.update(id, request);
    }

    @DgsMutation
    public Boolean deleteResponsible(@InputArgument Long id) {
        service.delete(id);
        return true;
    }
}
