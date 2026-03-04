package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.component.JwtUtil;
import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import br.com.setecolinas.kanban_project.dto.LoginRequestDTO;
import br.com.setecolinas.kanban_project.dto.RegisterRequestDTO;
import br.com.setecolinas.kanban_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints para autenticação, registro e geração de token JWT")
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    // Explicit constructor to ensure it's generated
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar novo usuário", description = "Cria novo usuário e organização, retorna token JWT")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Requisição de registro recebida para: {}", request.email());
        AuthResponseDTO response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login de usuário", description = "Autentica usuário e retorna token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Requisição de login recebida para: {}", request.email());
        AuthResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
