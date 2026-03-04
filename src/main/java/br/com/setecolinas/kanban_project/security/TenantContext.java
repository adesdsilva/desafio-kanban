package br.com.setecolinas.kanban_project.security;

import br.com.setecolinas.kanban_project.model.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class TenantContext {

    public static String getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getTenantId();
        }

        throw new IllegalStateException("Nenhum tenant encontrado no contexto de segurança");
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }

        throw new IllegalStateException("Nenhum usuário encontrado no contexto de segurança");
    }

    public static Long getCurrentOrganizationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getOrganization().getId();
        }

        throw new IllegalStateException("Nenhuma organização encontrada no contexto de segurança");
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        throw new IllegalStateException("Nenhum usuário encontrado no contexto de segurança");
    }
}

