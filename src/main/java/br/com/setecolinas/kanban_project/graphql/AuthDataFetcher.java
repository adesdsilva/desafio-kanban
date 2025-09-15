package br.com.setecolinas.kanban_project.graphql;

import br.com.setecolinas.kanban_project.component.JwtUtil;
import br.com.setecolinas.kanban_project.dto.AuthRequestDTO;
import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;

@DgsComponent
public class AuthDataFetcher {

    private final JwtUtil jwtUtil;

    public AuthDataFetcher(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @DgsMutation
    public AuthResponseDTO login(@InputArgument AuthRequestDTO request) {
        if ("admin".equals(request.username()) && "admin".equals(request.password())) {
            String token = jwtUtil.generateToken(request.username());
            return new AuthResponseDTO(token);
        }

        throw new RuntimeException("Credenciais inválidas");
    }
}