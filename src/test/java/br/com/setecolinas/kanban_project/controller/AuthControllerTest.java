package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import br.com.setecolinas.kanban_project.dto.LoginRequestDTO;
import br.com.setecolinas.kanban_project.dto.RegisterRequestDTO;
import br.com.setecolinas.kanban_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AuthController")
class AuthControllerTest {

    @Mock
    private UserService userService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(userService);
    }

    @Test
    @DisplayName("Deve retornar 201 Created e token para registro válido")
    void register_withValidCredentials_shouldReturnCreatedAndToken() {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO(
                "João Silva",
                "joao@example.com",
                "senha123",
                "Minha Empresa",
                "minha-empresa"
        );

        AuthResponseDTO mockResponse = new AuthResponseDTO(
                "mocked_jwt_token",
                1L,
                "joao@example.com",
                "João Silva",
                "ORG_ADMIN",
                1L,
                "Minha Empresa",
                "tenant-123",
                Instant.now().plusSeconds(86400)
        );

        when(userService.register(any(RegisterRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mocked_jwt_token", response.getBody().token());
        assertEquals("joao@example.com", response.getBody().email());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e token para login válido")
    void login_withValidCredentials_shouldReturnOkAndToken() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("joao@example.com", "senha123");

        AuthResponseDTO mockResponse = new AuthResponseDTO(
                "mocked_jwt_token",
                1L,
                "joao@example.com",
                "João Silva",
                "ORG_ADMIN",
                1L,
                "Minha Empresa",
                "tenant-123",
                Instant.now().plusSeconds(86400)
        );

        when(userService.login(any(LoginRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mocked_jwt_token", response.getBody().token());
        assertEquals("joao@example.com", response.getBody().email());
    }

    @Test
    @DisplayName("Deve retornar erro para credenciais inválidas")
    void login_withInvalidCredentials_shouldThrowException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("invalido@example.com", "senha_errada");

        when(userService.login(any(LoginRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Usuário ou senha inválidos"));

        // Act & Assert
        try {
            authController.login(request);
        } catch (IllegalArgumentException e) {
            assertEquals("Usuário ou senha inválidos", e.getMessage());
        }
    }
}
