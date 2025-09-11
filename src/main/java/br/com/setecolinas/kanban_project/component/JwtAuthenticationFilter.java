package br.com.setecolinas.kanban_project.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String userId = jwtUtil.extractUserId(token);

                // Colocar userId no SecurityContext
                var authToken = new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Colocar userId no MDC para logs estruturados
                MDC.put("userId", userId);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Sempre limpar o MDC
            MDC.remove("userId");
        }
    }
}


