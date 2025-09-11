package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.service.SecretariaService;
import com.netflix.graphql.dgs.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class SecretariaDataFetcher {

    private final SecretariaService secretariaService;

    public SecretariaDataFetcher(SecretariaService secretariaService) {
        this.secretariaService = secretariaService;
    }

    // --- Queries ---
    @DgsQuery
    public Page<SecretariaResponseDTO> secretarias(@InputArgument Integer page,
                                                   @InputArgument Integer size) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;
        return secretariaService.findAll(PageRequest.of(p, s));
    }

    @DgsQuery
    public SecretariaResponseDTO secretaria(@InputArgument Long id) {
        return secretariaService.findById(id);
    }

    // --- Mutations ---
    @DgsMutation
    public SecretariaResponseDTO createSecretaria(@InputArgument(name = "request") SecretariaRequestDTO request) {
        return secretariaService.create(request);
    }

    @DgsMutation
    public SecretariaResponseDTO updateSecretaria(@InputArgument Long id,
                                                  @InputArgument(name = "request") SecretariaRequestDTO request) {
        return secretariaService.update(id, request);
    }

    @DgsMutation
    public Boolean deleteSecretaria(@InputArgument Long id) {
        secretariaService.delete(id);
        return true;
    }
}
