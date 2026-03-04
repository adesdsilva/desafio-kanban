package br.com.setecolinas.kanban_project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import br.com.setecolinas.kanban_project.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor());
    }

    @Slf4j
    public static class TenantInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                 @NonNull Object handler) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();

                    if (principal instanceof User) {
                        User user = (User) principal;
                        String tenantId = user.getTenantId();

                        // Colocar tenantId no contexto da requisição
                        request.setAttribute("tenantId", tenantId);
                        request.setAttribute("userId", user.getId());
                        request.setAttribute("organizationId", user.getOrganization().getId());

                        log.debug("TenantId setado para requisição: {}", tenantId);
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao processar tenant interceptor", e);
            }

            return true;
        }
    }
}

