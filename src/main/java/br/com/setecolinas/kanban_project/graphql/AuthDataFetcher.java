package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import br.com.setecolinas.kanban_project.dto.LoginRequestDTO;
import br.com.setecolinas.kanban_project.service.UserService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class AuthDataFetcher {

    private final UserService userService;

    @DgsMutation
    public AuthResponseDTO login(@InputArgument LoginRequestDTO request) {
        return userService.login(request);
    }
}