# 📋 Estrutura de Arquivos - Sprint 1 Completa

## 📂 Diretório do Projeto

```
kanban-project/
│
├── 📄 pom.xml (MODIFICADO)
│   └── ✅ Adicionadas dependências: BCrypt, Liquibase, Stripe, Lombok
│
├── 📁 src/main/java/br/com/setecolinas/kanban_project/
│   │
│   ├── 📁 model/ (CORE DATA MODELS)
│   │   ├── User.java [NOVO] ⭐
│   │   │   └── Implementa UserDetails, com roles e multi-tenant
│   │   ├── Organization.java [NOVO] ⭐
│   │   │   └── Entidade de tenant/empresa
│   │   ├── Subscription.java [NOVO] ⭐
│   │   │   └── Plano de assinatura por organização
│   │   ├── AuditLog.java [NOVO] ⭐
│   │   │   └── Registro de auditoria e ações
│   │   ├── UserRole.java [NOVO] ⭐
│   │   │   └── Enum: ADMIN, ORG_ADMIN, MEMBER
│   │   ├── PlanType.java [NOVO] ⭐
│   │   │   └── Enum: FREE, PRO, ENTERPRISE
│   │   └── Project.java [MODIFICADO]
│   │       └── Adicionado: tenantId, organization (FK)
│   │
│   ├── 📁 service/ (BUSINESS LOGIC)
│   │   ├── UserService.java [NOVO] ⭐
│   │   │   ├── register() - Criar usuário + org + subscription
│   │   │   ├── login() - Autenticar e gerar JWT
│   │   │   ├── updateProfile() - Atualizar perfil
│   │   │   ├── deactivateUser() - Desativar conta
│   │   │   └── changePassword() - Trocar senha
│   │   ├── AuditLogService.java [NOVO] ⭐
│   │   │   ├── log() - Registrar ação
│   │   │   ├── findByTenantId() - Buscar logs
│   │   │   └── findByTenantIdAndTimestampBetween() - Filtrar por período
│   │   ├── SubscriptionService.java [NOVO] ⭐
│   │   │   ├── findByOrganizationId() - Buscar subscription
│   │   │   ├── upgradeToPro() - Upgrade de plano
│   │   │   ├── downgradeToFree() - Downgrade de plano
│   │   │   └── canCreateProject() - Validar limite
│   │   ├── ProjectService.java [A IMPLEMENTAR - Sprint 2]
│   │   ├── ResponsibleService.java [EXISTENTE]
│   │   └── SecretariaService.java [EXISTENTE]
│   │
│   ├── 📁 repository/ (DATA ACCESS LAYER)
│   │   ├── UserRepository.java [NOVO] ⭐
│   │   │   ├── findByEmail()
│   │   │   ├── findByEmailAndTenantId()
│   │   │   ├── findByTenantId()
│   │   │   └── existsByEmail()
│   │   ├── OrganizationRepository.java [NOVO] ⭐
│   │   │   ├── findByTenantId()
│   │   │   └── findBySlug()
│   │   ├── SubscriptionRepository.java [NOVO] ⭐
│   │   │   ├── findByOrganizationId()
│   │   │   └── findByTenantId()
│   │   ├── AuditLogRepository.java [NOVO] ⭐
│   │   │   ├── findByTenantId()
│   │   │   └── findByTenantIdAndTimestampBetween()
│   │   ├── ProjectRepository.java [EXISTENTE]
│   │   ├── ResponsibleRepository.java [EXISTENTE]
│   │   └── SecretariaRepository.java [EXISTENTE]
│   │
│   ├── 📁 security/ (AUTHENTICATION & JWT)
│   │   ├── JwtTokenProvider.java [NOVO] ⭐
│   │   │   ├── generateToken() - Gerar JWT
│   │   │   ├── validateToken() - Validar JWT
│   │   │   ├── getTenantIdFromToken() - Extrair tenantId
│   │   │   └── getExpirationDate() - Expiração
│   │   ├── JwtAuthenticationFilter.java [NOVO] ⭐
│   │   │   └── doFilterInternal() - Filtrar requisições
│   │   └── TenantContext.java [NOVO] ⭐
│   │       ├── getCurrentTenantId() - Obter tenant atual
│   │       ├── getCurrentUserId() - Obter user atual
│   │       └── getCurrentUser() - Obter usuário completo
│   │
│   ├── 📁 controller/ (REST ENDPOINTS)
│   │   ├── AuthController.java [MODIFICADO]
│   │   │   ├── POST /api/auth/register - Registrar
│   │   │   └── POST /api/auth/login - Login
│   │   ├── ProjectController.java [EXISTENTE]
│   │   ├── ResponsibleController.java [EXISTENTE]
│   │   └── SecretariaController.java [EXISTENTE]
│   │
│   ├── 📁 config/ (SPRING CONFIGURATION)
│   │   ├── SecurityConfig.java [MODIFICADO]
│   │   │   ├── PasswordEncoder Bean
│   │   │   ├── AuthenticationManager Bean
│   │   │   └── SecurityFilterChain Bean
│   │   ├── TenantInterceptorConfig.java [NOVO] ⭐
│   │   │   └── TenantInterceptor - Popula tenantId
│   │   └── Outras configs [EXISTENTES]
│   │
│   ├── 📁 dto/ (DATA TRANSFER OBJECTS)
│   │   ├── RegisterRequestDTO.java [NOVO] ⭐
│   │   │   └── name, email, password, organizationName, organizationSlug
│   │   ├── LoginRequestDTO.java [NOVO] ⭐
│   │   │   └── email, password
│   │   ├── AuthResponseDTO.java [MODIFICADO]
│   │   │   └── token, userId, email, name, role, organizationId, tenantId, expiresAt
│   │   └── Outras DTOs [EXISTENTES]
│   │
│   ├── 📁 exceptions/ (ERROR HANDLING)
│   │   ├── ResourceNotFoundException.java [NOVO] ⭐
│   │   │   └── Extends NotFoundException
│   │   ├── NotFoundException.java [EXISTENTE]
│   │   ├── BusinessException.java [EXISTENTE]
│   │   └── GlobalExceptionHandler.java [EXISTENTE]
│   │
│   ├── 📁 graphql/ (GRAPHQL RESOLVERS)
│   │   ├── AuthDataFetcher.java [MODIFICADO]
│   │   │   └── Atualizado para usar novo UserService
│   │   └── Outras fetchers [EXISTENTES]
│   │
│   └── KanbanProjectApplication.java [EXISTENTE]
│
├── 📁 src/main/resources/
│   ├── application.yml [MODIFICADO]
│   │   └── ✅ Adicionado JWT_SECRET e JWT_EXPIRATION
│   ├── application-dev.yml [EXISTENTE]
│   ├── application-docker.yml [EXISTENTE]
│   ├── logback-spring.xml [EXISTENTE]
│   ├── prometheus.yml [EXISTENTE]
│   └── graphql/ [EXISTENTE]
│
├── 📁 src/test/java/br/com/setecolinas/kanban_project/
│   ├── UserServiceTest.java [NOVO] ⭐
│   │   └── 13 testes unitários de exemplo
│   ├── AuthControllerTest.java [EXISTENTE]
│   ├── ProjectControllerTest.java [EXISTENTE]
│   └── Outros testes [EXISTENTES]
│
├── 📁 target/ (BUILD OUTPUT)
│   ├── classes/ ✅ Classes compiladas
│   ├── kanban-project-0.0.1-SNAPSHOT.jar ✅
│   └── ...
│
├── 📁 logs/
│   ├── kanban-project.log
│   └── kanban-project.2025-*.log.gz
│
├── 📄 docker-compose.yml [EXISTENTE]
├── 📄 Dockerfile [EXISTENTE]
│
├── 📚 DOCUMENTAÇÃO SPRINT 1
│   ├── SAAS_IMPLEMENTATION.md [NOVO] ⭐
│   │   └── Documentação técnica completa (5 sprints)
│   ├── QUICKSTART_SAAS.md [NOVO] ⭐
│   │   └── Guia rápido de uso
│   ├── SPRINT1_SUMMARY.md [NOVO] ⭐
│   │   └── Resumo executivo
│   ├── README_SPRINT1.md [NOVO] ⭐
│   │   └── Guia completo de implementação
│   ├── FILES_STRUCTURE.md [ESTE ARQUIVO]
│   │   └── Estrutura de arquivos
│   ├── README.md [EXISTENTE]
│   ├── HELP.md [EXISTENTE]
│   └── ...outros [EXISTENTES]
│
└── 📄 mvnw, mvnw.cmd (Maven Wrapper)

```

---

## ✅ Checklist de Arquivos Criados/Modificados

### 🟢 CRIADOS (21 arquivos)

#### Models (6)
- [x] User.java
- [x] Organization.java
- [x] Subscription.java
- [x] AuditLog.java
- [x] UserRole.java
- [x] PlanType.java

#### Services (3)
- [x] UserService.java
- [x] AuditLogService.java
- [x] SubscriptionService.java

#### Repositories (4)
- [x] UserRepository.java
- [x] OrganizationRepository.java
- [x] SubscriptionRepository.java
- [x] AuditLogRepository.java

#### Security (3)
- [x] JwtTokenProvider.java
- [x] JwtAuthenticationFilter.java
- [x] TenantContext.java

#### DTOs (3)
- [x] RegisterRequestDTO.java
- [x] LoginRequestDTO.java

#### Config (1)
- [x] TenantInterceptorConfig.java

#### Exceptions (1)
- [x] ResourceNotFoundException.java

#### Tests (1)
- [x] UserServiceTest.java

#### Documentação (4)
- [x] SAAS_IMPLEMENTATION.md
- [x] QUICKSTART_SAAS.md
- [x] SPRINT1_SUMMARY.md
- [x] README_SPRINT1.md

### 🟡 MODIFICADOS (5 arquivos)

- [x] pom.xml - Adicionar dependências
- [x] SecurityConfig.java - Atualizar configuração
- [x] AuthController.java - Atualizar endpoints
- [x] AuthResponseDTO.java - Expandir resposta
- [x] Project.java - Adicionar tenantId
- [x] application.yml - Adicionar JWT config
- [x] AuthDataFetcher.java - Atualizar para novo UserService

---

## 📊 Resumo por Tipo de Arquivo

| Tipo | Criados | Modificados | Total |
|------|---------|-------------|-------|
| Models | 6 | 1 | 7 |
| Services | 3 | 0 | 3 |
| Repositories | 4 | 0 | 4 |
| Security | 3 | 0 | 3 |
| Controllers | 0 | 1 | 1 |
| Config | 1 | 1 | 2 |
| DTOs | 2 | 1 | 3 |
| Tests | 1 | 0 | 1 |
| Exceptions | 1 | 0 | 1 |
| Docs | 4 | 0 | 4 |
| **TOTAL** | **25** | **4** | **29** |

---

## 🔄 Dependências Adicionadas

```xml
<!-- Spring Security Crypto (BCrypt) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>

<!-- Liquibase (Database Migrations) -->
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>

<!-- Stripe SDK -->
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>25.2.0</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🚀 Próximos Passos - Sprint 2

### Tasks para Sprint 2

```
1. MIGRAÇÃO DE ENTIDADES
   [ ] Adicionar tenantId a Project
   [ ] Adicionar tenantId a Responsible
   [ ] Adicionar tenantId a Secretaria
   [ ] Criar índices para multi-tenant
   
2. REPOSITORIES
   [ ] Criar ProjectRepository com queries multi-tenant
   [ ] Atualizar ResponsibleRepository
   [ ] Atualizar SecretariaRepository
   
3. SERVICES
   [ ] Implementar ProjectService
   [ ] Atualizar ResponsibleService
   [ ] Atualizar SecretariaService
   [ ] Adicionar filtros automáticos por tenant
   
4. CONTROLLERS
   [ ] Criar ProjectController
   [ ] Atualizar ResponsibleController
   [ ] Atualizar SecretariaController
   [ ] Adicionar @PreAuthorize em endpoints
   
5. TESTES
   [ ] ProjectServiceTest (com multi-tenant)
   [ ] ProjectControllerTest
   [ ] ResponsibleServiceTest
   [ ] ResponsibleControllerTest
   
6. DOCUMENTAÇÃO
   [ ] Atualizar Swagger/OpenAPI
   [ ] Documentar novos endpoints
   [ ] Guia de uso de Project com multi-tenant
```

### Arquivos a Criar em Sprint 2

```
NOVO:
├── src/main/java/.../service/ProjectService.java
├── src/main/java/.../repository/ProjectRepository.java (updates)
├── src/main/java/.../controller/ProjectController.java (updates)
├── src/test/java/.../service/ProjectServiceTest.java
└── src/test/java/.../controller/ProjectControllerTest.java

MODIFICAR:
├── src/main/java/.../model/Project.java (add tenantId)
├── src/main/java/.../model/Responsible.java (add tenantId)
├── src/main/java/.../model/Secretaria.java (add tenantId)
├── src/main/resources/application.yml (new profiles)
└── pom.xml (if needed - add migration tool)
```

---

## 📈 Progressão do Projeto

```
Sprint 0: Preparação ✅
├── Estrutura base ✅
├── Dependências ✅
└── DTOs base ✅

Sprint 1: Autenticação & Multi-Tenant ✅ (VOCÊ ESTÁ AQUI)
├── User model ✅
├── Organization model ✅
├── JWT authentication ✅
├── Multi-tenant isolation ✅
├── Auditoria ✅
└── Documentação ✅

Sprint 2: Migração de Entidades (PRÓXIMO)
├── Project + tenantId
├── Responsible + tenantId
├── Secretaria + tenantId
├── ProjectService
├── ProjectController
└── Testes

Sprint 3: Planos & Limites
├── Validação de limites
├── Integração Stripe (mock)
├── Payment flow
└── Upgrade/Downgrade

Sprint 4: QA & Deploy
├── Testes de integração
├── CI/CD
├── Documentação final
└── Deploy em produção

Sprint 5: Segurança & Observabilidade
├── Rate limiting
├── Pen testing
├── Monitoring
└── Alertas
```

---

## 🎯 Como Usar Este Mapa

1. **Para entender a estrutura**: Use este arquivo
2. **Para documentação técnica**: Leia SAAS_IMPLEMENTATION.md
3. **Para começar rápido**: Leia QUICKSTART_SAAS.md
4. **Para implementação completa**: Leia README_SPRINT1.md

---

## 📝 Comandos Úteis

```bash
# Build
./mvnw clean compile

# Testes
./mvnw test -Dtest=UserServiceTest

# Package
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run

# Docker
docker-compose up

# Ver logs
tail -f logs/kanban-project.log

# Health check
curl http://localhost:8081/actuator/health

# Swagger
http://localhost:8081/swagger-ui.html
```

---

## 💡 Notas Importantes

1. **Java Version**: Downgrade de 21 para 17 por compatibilidade
2. **JWT Secret**: Mudar em produção via variável de ambiente
3. **Database**: Criar tabelas via Hibernate (ddl-auto: update)
4. **Multi-Tenant**: Filtro automático em toda requisição
5. **Auditoria**: Todos os logins e ações críticas são registrados

---

**Data**: 2026-03-04  
**Status**: ✅ Sprint 1 Completa  
**Próximo**: Sprint 2 - Migração de Entidades

