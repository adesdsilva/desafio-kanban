package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.AuthResponseDTO;
import br.com.setecolinas.kanban_project.dto.LoginRequestDTO;
import br.com.setecolinas.kanban_project.dto.RegisterRequestDTO;
import br.com.setecolinas.kanban_project.exceptions.ResourceNotFoundException;
import br.com.setecolinas.kanban_project.model.*;
import br.com.setecolinas.kanban_project.model.enums.PlanType;
import br.com.setecolinas.kanban_project.model.enums.UserRole;
import br.com.setecolinas.kanban_project.repository.OrganizationRepository;
import br.com.setecolinas.kanban_project.repository.SubscriptionRepository;
import br.com.setecolinas.kanban_project.repository.UserRepository;
import br.com.setecolinas.kanban_project.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Iniciando registro de novo usuário: {}", request.email());

        // Validar se email já existe
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email já registrado: {}", request.email());
            throw new IllegalArgumentException("Email já está registrado");
        }

        // Validar se slug da organização já existe
        if (organizationRepository.existsBySlug(request.organizationSlug())) {
            log.warn("Slug de organização já existe: {}", request.organizationSlug());
            throw new IllegalArgumentException("Slug da organização já está em uso");
        }

        // Criar organização
        String tenantId = UUID.randomUUID().toString();
        Organization organization = Organization.builder()
                .name(request.organizationName())
                .slug(request.organizationSlug())
                .tenantId(tenantId)
                .build();
        organization = organizationRepository.save(organization);
        log.info("Organização criada: {} (tenantId: {})", organization.getName(), tenantId);

        // Criar subscription FREE padrão
        Subscription subscription = Subscription.builder()
                .organization(organization)
                .plan(PlanType.FREE)
                .tenantId(tenantId)
                .build();
        subscriptionRepository.save(subscription);
        log.info("Plano FREE criado para organização: {}", organization.getId());

        // Criar usuário
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .organization(organization)
                .tenantId(tenantId)
                .role(UserRole.ORG_ADMIN)
                .active(true)
                .build();
        user = userRepository.save(user);
        log.info("Usuário criado: {} (userId: {})", user.getEmail(), user.getId());

        // Registrar auditoria
        auditLogService.log(tenantId, user, "REGISTER", "User", user.getId(),
                "Novo usuário registrado e adicionado como ORG_ADMIN");

        // Gerar token JWT
        String token = jwtTokenProvider.generateToken(user);
        Instant expiresAt = jwtTokenProvider.getExpirationDate(token);

        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                organization.getId(),
                organization.getName(),
                tenantId,
                expiresAt
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Tentativa de login para: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: {}", request.email());
                    return new ResourceNotFoundException("Usuário ou senha inválidos");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Senha inválida para usuário: {}", request.email());
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        if (!user.getActive()) {
            log.warn("Tentativa de login com usuário inativo: {}", request.email());
            throw new IllegalArgumentException("Usuário está inativo");
        }

        // Atualizar último login
        user.setLastLogin(Instant.now());
        user = userRepository.save(user);
        log.info("Login bem-sucedido para: {}", user.getEmail());

        // Registrar auditoria
        auditLogService.log(user.getTenantId(), user, "LOGIN", "User", user.getId(),
                "Usuário realizou login");

        // Gerar token JWT
        String token = jwtTokenProvider.generateToken(user);
        Instant expiresAt = jwtTokenProvider.getExpirationDate(token);

        Organization organization = user.getOrganization();
        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                organization.getId(),
                organization.getName(),
                user.getTenantId(),
                expiresAt
        );
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public void updateProfile(Long userId, String name, String email) {
        User user = findById(userId);

        // Validar email único
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        auditLogService.log(user.getTenantId(), user, "UPDATE_PROFILE", "User", user.getId(),
                "Perfil do usuário atualizado");
        log.info("Perfil do usuário atualizado: {}", userId);
    }

    public void deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);

        auditLogService.log(user.getTenantId(), user, "DEACTIVATE", "User", user.getId(),
                "Usuário desativado");
        log.info("Usuário desativado: {}", userId);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Senha atual está incorreta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogService.log(user.getTenantId(), user, "CHANGE_PASSWORD", "User", user.getId(),
                "Senha do usuário alterada");
        log.info("Senha alterada para usuário: {}", userId);
    }
}

