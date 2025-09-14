package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.component.JwtUtil;
import br.com.setecolinas.kanban_project.dto.AuthRequestDTO;
import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AuthController")
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(jwtUtil);
    }

    @Test
    @DisplayName("Deve retornar 200 OK e token para credenciais válidas")
    void login_withValidCredentials_shouldReturnOkAndToken() {
        AuthRequestDTO request = new AuthRequestDTO("admin", "admin");
        when(jwtUtil.generateToken("admin")).thenReturn("mocked_jwt_token");

        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mocked_jwt_token", response.getBody().token());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized para credenciais inválidas")
    void login_withInvalidCredentials_shouldReturnUnauthorized() {
        AuthRequestDTO request = new AuthRequestDTO("user_invalido", "senha_invalida");

        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
