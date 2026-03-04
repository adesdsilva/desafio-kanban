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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO(
                "João Silva",
                "joao@example.com",
                "senha123",
                "Empresa XYZ",
                "empresa-xyz"
        );

        loginRequest = new LoginRequestDTO(
                "joao@example.com",
                "senha123"
        );
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterNewUserSuccess() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(organizationRepository.existsBySlug(registerRequest.organizationSlug())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashed-password");
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationDate("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

        Organization org = Organization.builder()
                .id(1L)
                .name(registerRequest.organizationName())
                .slug(registerRequest.organizationSlug())
                .tenantId("tenant-123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password("hashed-password")
                .organization(org)
                .tenantId("tenant-123")
                .role(UserRole.ORG_ADMIN)
                .build();

        when(organizationRepository.save(any())).thenReturn(org);
        when(subscriptionRepository.save(any())).thenReturn(Subscription.builder()
                .id(1L)
                .organization(org)
                .plan(PlanType.FREE)
                .tenantId("tenant-123")
                .build());
        when(userRepository.save(any())).thenReturn(savedUser);

        // When
        AuthResponseDTO response = userService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(registerRequest.email());
        assertThat(response.role()).isEqualTo(UserRole.ORG_ADMIN.name());
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tenantId()).isEqualTo("tenant-123");

        verify(userRepository).existsByEmail(registerRequest.email());
        verify(organizationRepository).save(any());
        verify(subscriptionRepository).save(any());
        verify(userRepository).save(any());
        verify(auditLogService).log(anyString(), any(), anyString(), anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void testRegisterDuplicateEmail() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já está registrado");

        verify(userRepository).existsByEmail(registerRequest.email());
        verify(organizationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail when organization slug already exists")
    void testRegisterDuplicateSlug() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(organizationRepository.existsBySlug(registerRequest.organizationSlug())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Slug da organização já está em uso");
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        // Given
        Organization org = Organization.builder().id(1L).name("Test Org").build();
        User user = User.builder()
                .id(1L)
                .email("joao@example.com")
                .password("hashed-password")
                .active(true)
                .organization(org)
                .tenantId("tenant-123")
                .role(UserRole.MEMBER)
                .build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationDate("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

        // When
        AuthResponseDTO response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("joao@example.com");
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tenantId()).isEqualTo("tenant-123");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verify(auditLogService).log(anyString(), any(), eq("LOGIN"), anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should fail login with invalid password")
    void testLoginInvalidPassword() {
        // Given
        Organization org = Organization.builder().id(1L).build();
        User user = User.builder()
                .email("joao@example.com")
                .password("hashed-password")
                .active(true)
                .organization(org)
                .build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário ou senha inválidos");
    }

    @Test
    @DisplayName("Should fail login with invalid email")
    void testLoginUserNotFound() {
        // Given
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário ou senha inválidos");
    }

    @Test
    @DisplayName("Should fail login when user is inactive")
    void testLoginInactiveUser() {
        // Given
        Organization org = Organization.builder().id(1L).build();
        User user = User.builder()
                .email("joao@example.com")
                .password("hashed-password")
                .active(false)
                .organization(org)
                .build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário está inativo");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("joao@example.com")
                .build();

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));

        // When
        User result = userService.findByEmail("joao@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found by email")
    void testFindByEmailNotFound() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findByEmail("notfound@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    @DisplayName("Should update user profile")
    void testUpdateProfile() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .tenantId("tenant-123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("novoemail@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // When
        userService.updateProfile(1L, "João Silva Updated", "novoemail@example.com");

        // Then
        assertThat(user.getName()).isEqualTo("João Silva Updated");
        assertThat(user.getEmail()).isEqualTo("novoemail@example.com");
        verify(userRepository).save(user);
        verify(auditLogService).log(anyString(), any(), eq("UPDATE_PROFILE"), anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should deactivate user")
    void testDeactivateUser() {
        // Given
        User user = User.builder()
                .id(1L)
                .active(true)
                .tenantId("tenant-123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        // When
        userService.deactivateUser(1L);

        // Then
        assertThat(user.getActive()).isFalse();
        verify(userRepository).save(user);
        verify(auditLogService).log(anyString(), any(), eq("DEACTIVATE"), anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should change user password")
    void testChangePassword() {
        // Given
        User user = User.builder()
                .id(1L)
                .password("old-hashed-password")
                .tenantId("tenant-123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpassword", "old-hashed-password")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("new-hashed-password");
        when(userRepository.save(any())).thenReturn(user);

        // When
        userService.changePassword(1L, "oldpassword", "newpassword");

        // Then
        assertThat(user.getPassword()).isEqualTo("new-hashed-password");
        verify(userRepository).save(user);
        verify(auditLogService).log(anyString(), any(), eq("CHANGE_PASSWORD"), anyString(), anyLong(), anyString());
    }
}

