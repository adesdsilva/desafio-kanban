package br.com.setecolinas.kanban_project.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

