package br.com.setecolinas.kanban_project.security;

import br.com.setecolinas.kanban_project.model.User;
import br.com.setecolinas.kanban_project.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String email = tokenProvider.getUsernameFromToken(jwt);
                String tenantId = tokenProvider.getTenantIdFromToken(jwt);

                Optional<User> user = userRepository.findByEmailAndTenantId(email, tenantId);

                if (user.isPresent() && user.get().isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Colocar tenantId no request para usar em filtros posteriores
                    request.setAttribute("tenantId", tenantId);
                    log.debug("JWT validado para usuário: {} (tenant: {})", email, tenantId);
                } else {
                    log.warn("Usuário não encontrado ou inativo: {} (tenant: {})", email, tenantId);
                }
            }
        } catch (Exception ex) {
            log.error("Erro ao processar JWT", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

