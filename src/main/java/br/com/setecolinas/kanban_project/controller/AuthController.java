package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.component.JwtUtil;
import br.com.setecolinas.kanban_project.dto.AuthRequestDTO;
import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        if ("admin".equals(request.username()) && "admin".equals(request.password())) {
            String token = jwtUtil.generateToken(request.username());
            return ResponseEntity.ok(new AuthResponseDTO(token));
        }

        return ResponseEntity.status(401).build();
    }
}

