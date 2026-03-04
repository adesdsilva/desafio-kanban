# Transformação para SaaS - Guia de Implementação

## 📋 Status: Sprint 1 Completa ✅

Este documento descreve a transformação do Kanban em uma solução SaaS profissional com autenticação, multi-tenancy e planos de assinatura.

---

## 🎯 O que foi implementado - Sprint 1

### ✅ Autenticação e Autorização

#### Modelos
- **User**: Usuário com roles (ADMIN, ORG_ADMIN, MEMBER)
  - Implementa `UserDetails` do Spring Security
  - Campos: name, email, password, role, organization, tenantId, active, lastLogin
  - Auditoria: createdAt, updatedAt

#### Serviços
- **UserService**: Gerencia registro, login e perfil de usuários
  - `register()`: Cria novo usuário + organização + subscription FREE
  - `login()`: Autentica e gera JWT
  - `updateProfile()`: Atualiza dados do usuário
  - `deactivateUser()`: Desativa conta
  - `changePassword()`: Altera senha com validação

#### Segurança
- **JwtTokenProvider**: Gera e valida tokens JWT
  - Incluir: userId, organizationId, tenantId, role
  - Expiração: 24 horas (configurável)
  - Algoritmo: HS256 (produção: considerar RS256)

- **JwtAuthenticationFilter**: Filtro que valida JWT em cada requisição
  - Extrai claims do token
  - Popula SecurityContext com usuário autenticado
  - Define atributos de tenantId na requisição

- **SecurityConfig**: Configuração Spring Security
  - PasswordEncoder: BCrypt
  - Endpoints públicos: `/api/auth/register`, `/api/auth/login`, Swagger, Actuator
  - Filtro de autenticação adicionado antes de `UsernamePasswordAuthenticationFilter`

#### Controllers
- **AuthController**: 
  - `POST /api/auth/register`: Registra novo usuário
  - `POST /api/auth/login`: Faz login
  - Retorna `AuthResponseDTO` com token e dados do usuário

---

### ✅ Multi-Tenant

#### Modelos
- **Organization**: Representa empresa/tenant
  - Campos: name, slug, tenantId (UUID), logo, website
  - Relacionamento 1:N com User
  - Índices para performance: tenant_id, slug

- **Subscription**: Plano de assinatura
  - Relacionamento 1:1 com Organization
  - Campo: plan (FREE/PRO/ENTERPRISE)
  - Stripe: customerId, subscriptionId (para futura integração)

#### Enums
- **PlanType**:
  - FREE: até 3 projetos
  - PRO: ilimitado com features avançadas
  - ENTERPRISE: customizado

#### Serviços
- **TenantContext**: Utilitário para acessar tenant atual
  - `getCurrentTenantId()`: Obtém tenantId do contexto de segurança
  - `getCurrentUserId()`: Obtém userId
  - `getCurrentOrganizationId()`: Obtém organizationId
  - `getCurrentUser()`: Obtém usuário completo

- **SubscriptionService**: Gerencia planos
  - `findByOrganizationId()`: Busca subscription
  - `upgradeToPro()`: Upgrade de plano
  - `downgradeToFree()`: Downgrade de plano
  - `canCreateProject()`: Valida limite de projetos
  - `hasAdvancedFeatures()`: Verifica features ativas

#### Interceptor
- **TenantInterceptorConfig**: Popula atributos de tenant em cada requisição
  - Extrai tenantId do usuário autenticado
  - Define: tenantId, userId, organizationId

---

### ✅ Auditoria e Logging

#### Modelos
- **AuditLog**: Registra todas as ações importantes
  - Campos: tenantId, user, action, entityType, entityId, description, ipAddress, timestamp
  - Índices para queries eficientes

#### Serviços
- **AuditLogService**: Gerencia registros de auditoria
  - `log()`: Registra uma ação (automático obter IP)
  - `findByTenantId()`: Busca logs por tenant
  - `findByTenantIdAndUserId()`: Busca logs por usuário
  - `findByTenantIdAndTimestampBetween()`: Busca por período

---

### ✅ DTOs

- **RegisterRequestDTO**: Email, password, name, organizationName, organizationSlug
- **LoginRequestDTO**: Email, password
- **AuthResponseDTO**: token, userId, email, name, role, organizationId, organizationName, tenantId, expiresAt

---

### ✅ Repositories

- **UserRepository**: Busca por email, tenantId, organização
- **OrganizationRepository**: Busca por tenantId, slug
- **SubscriptionRepository**: Busca por organizationId, tenantId, Stripe customerId
- **AuditLogRepository**: Queries de auditoria

---

## 🔧 Configuração

### application.yml

```yaml
jwt:
  secret: mySuperSecretKeyThatIsAtLeast32CharactersLongForHS256
  expiration: 86400000  # 24 horas em ms
```

**Importante**: Em produção, usar variáveis de ambiente:
```bash
JWT_SECRET=seu-secret-seguro-aqui
JWT_EXPIRATION=86400000
```

---

## 🚀 Como usar

### 1. Registrar novo usuário/organização

```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "organizationName": "Empresa XYZ",
  "organizationSlug": "empresa-xyz"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "joao@example.com",
  "name": "João Silva",
  "role": "ORG_ADMIN",
  "organizationId": 1,
  "organizationName": "Empresa XYZ",
  "tenantId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "expiresAt": "2026-03-05T09:48:00Z"
}
```

### 2. Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}

Response: [mesmo formato de registrar]
```

### 3. Usar token em requisições

```bash
GET /api/projects
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔐 Segurança - Boas Práticas

### Senha
- ✅ Hashed com BCrypt (force factor: 10)
- ✅ Mínimo 6 caracteres (considerar aumentar para 8+)
- ✅ Validação de complexidade (futuro: letras, números, símbolos)

### JWT
- ✅ HS256 com secret de 32+ caracteres
- ✅ Expiração: 24 horas
- ✅ Refresh token (futuro: implementar para melhor UX)

### Multi-Tenant
- ✅ Filtro automático por tenantId
- ✅ Validação em cada operação
- ✅ Índices de DB para performance

### Auditoria
- ✅ Todos os logins registrados
- ✅ Alterações de perfil rastreadas
- ✅ IP do cliente capturado

---

## 📊 Próximas Steps - Sprint 2

### Objetivos
1. ✅ Migrar entidades existentes (Project, Responsible, Secretaria) para multi-tenant
2. ✅ Criar validadores de tenant em todos os endpoints
3. ✅ Implementar ProjectService com filtros automáticos
4. ✅ Testes unitários de autenticação

### Tasks
- [ ] Adicionar tenantId a Project, Responsible, Secretaria
- [ ] Criar ProjectRepository com queries multi-tenant
- [ ] Implementar ProjectService
- [ ] Criar ProjectController com @PreAuthorize
- [ ] Testes: UserServiceTest, ProjectServiceTest
- [ ] Documentação de API (Swagger)

---

## 🧪 Testing

### Testes necessários (Sprint 4)

```java
@SpringBootTest
class UserServiceTest {
    // Testes de registro
    @Test void testRegisterNewUser() { }
    @Test void testRegisterDuplicateEmail() { }
    @Test void testRegisterCreatesOrganization() { }
    
    // Testes de login
    @Test void testLoginSuccess() { }
    @Test void testLoginInvalidPassword() { }
    @Test void testLoginUserNotFound() { }
    
    // Testes de multi-tenant
    @Test void testUserIsolatedByTenant() { }
}
```

---

## 📝 Checklist de Implementação

### Sprint 1 ✅
- [x] Modelos (User, Organization, Subscription, AuditLog)
- [x] Enums (UserRole, PlanType)
- [x] Repositories
- [x] UserService (register, login, profile)
- [x] JwtTokenProvider e JwtAuthenticationFilter
- [x] SecurityConfig
- [x] AuthController
- [x] AuditLogService
- [x] TenantContext
- [x] SubscriptionService
- [x] Configuração JWT

### Sprint 2 (Em Progress)
- [ ] Migrar Project/Responsible/Secretaria
- [ ] ProjectService
- [ ] ProjectController
- [ ] Testes unitários

### Sprint 3 (Future)
- [ ] Validação de limites de plano
- [ ] Webhook Stripe
- [ ] Payment integration

### Sprint 4 (Future)
- [ ] Testes de integração
- [ ] Documentação de API
- [ ] Deploy

---

## 🚨 Problemas Conhecidos

1. **Warn Lombok**: `@Builder.Default` em Organization.subscription
   - Status: ✅ Corrigido
   
2. **Java 21 não disponível**: Downgrade para Java 17
   - Status: ✅ Corrigido

---

## 📚 Referências

- Spring Security: https://spring.io/projects/spring-security
- JWT (JJWT): https://github.com/jwtk/jjwt
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Lombok: https://projectlombok.org/

---

## 📞 Suporte

Para dúvidas ou issues:
1. Verificar logs em `src/main/resources/logback-spring.xml`
2. Executar testes: `mvn test`
3. Build: `mvn clean package`
4. Docker: `docker-compose up`

---

**Última atualização**: 2026-03-04
**Status**: Sprint 1 Completa ✅

