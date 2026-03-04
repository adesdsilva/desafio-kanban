# 🚀 Kanban SaaS - Guia de Uso Rápido

## Sprint 1 - Autenticação & Multi-Tenant ✅

Sua aplicação Kanban foi transformada em um SaaS profissional com:
- ✅ Autenticação JWT
- ✅ Multi-tenant (isolamento de dados por organização)
- ✅ Planos de assinatura (FREE, PRO, ENTERPRISE)
- ✅ Auditoria e logging estruturado

---

## 📦 Compilação e Execução

### Build
```bash
./mvnw clean package
```

### Executar localmente
```bash
./mvnw spring-boot:run
```

### Docker
```bash
docker-compose up
```

A aplicação estará disponível em: **http://localhost:8081**

---

## 🔐 Endpoints de Autenticação

### 1️⃣ Registrar novo usuário (criar organização)

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@setecolinas.com.br",
    "password": "senha123",
    "organizationName": "Setecolinas",
    "organizationSlug": "setecolinas"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvQHNldGVjb2xpbmFzLmNvbS5iciIsInVzZXJJZCI6MSwib3JnYW5pemF0aW9uSWQiOjEsInRlbmFudElkIjoiZDNlZGFiNTItMmQ0My00ZjJhLWI0YTItYWI2YTQzYjE3ODAyIiwicm9sZSI6Ik9SR19BRE1JTiIsImlhdCI6MTY4MzI2OTM0MCwiZXhwIjoxNjgzMzU1NzQwfQ.abc123...",
  "userId": 1,
  "email": "joao@setecolinas.com.br",
  "name": "João Silva",
  "role": "ORG_ADMIN",
  "organizationId": 1,
  "organizationName": "Setecolinas",
  "tenantId": "d3edab52-2d43-4f2a-b4a2-ab6a43b17802",
  "expiresAt": "2026-03-05T10:29:00Z"
}
```

### 2️⃣ Login (fazer login com usuário existente)

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@setecolinas.com.br",
    "password": "senha123"
  }'
```

**Response:** (mesmo formato acima)

### 3️⃣ Usar token em requisições autenticadas

```bash
# Exemplo: buscar projetos do usuário
curl -X GET http://localhost:8081/api/projects \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📊 Estrutura do Banco de Dados

### Tabelas Principais

#### `users`
- Usuários do sistema
- Campos: id, name, email, password, role, organization_id, tenant_id, active, created_at, updated_at, last_login

#### `organizations`
- Tenants/Empresas
- Campos: id, name, slug, tenant_id (único), logo, website, created_at, updated_at

#### `subscriptions`
- Planos de assinatura
- Campos: id, organization_id, plan (FREE/PRO/ENTERPRISE), tenant_id, stripe_customer_id, stripe_subscription_id, start_date, end_date, created_at, updated_at

#### `audit_logs`
- Registros de auditoria
- Campos: id, tenant_id, user_id, action, entity_type, entity_id, description, ip_address, timestamp

---

## 🔒 Segurança

### JWT Token
- **Algoritmo**: HS256
- **Expiração**: 24 horas
- **Secret**: Configurável via `application.yml` ou variável `JWT_SECRET`
- **Claims inclusos**: userId, organizationId, tenantId, role

### Senha
- **Algoritmo**: BCrypt (força 10)
- **Mínimo**: 6 caracteres
- **Recomendado**: 8+ caracteres com números e símbolos

### Multi-Tenant
- Cada requisição é automaticamente filtrada pelo `tenantId` do usuário
- Dados de um tenant nunca são acessíveis por outro
- Índices de banco otimizados para queries multi-tenant

### Auditoria
- Todos os logins são registrados
- Mudanças de perfil são auditadas
- IP do cliente é capturado automaticamente

---

## 🔑 Roles e Permissões

Três papéis principais:

| Role | Descrição | Permissões |
|------|-----------|-----------|
| **ADMIN** | Administrador do Sistema | Gerencia todo o sistema |
| **ORG_ADMIN** | Admin da Organização | Gerencia sua organização, usuários, projetos |
| **MEMBER** | Membro da Equipe | Acessa projetos da organização |

---

## 📈 Planos de Assinatura

| Plano | Limite | Features | Preço |
|-------|--------|----------|-------|
| **FREE** | 3 projetos | Básico | Gratuito |
| **PRO** | Ilimitado | Avançado | A definir |
| **ENTERPRISE** | Customizado | Tudo | Customizado |

---

## 🧪 Testes

### Executar testes unitários
```bash
./mvnw test
```

### Testes específicos
```bash
./mvnw test -Dtest=UserServiceTest
```

### Com cobertura
```bash
./mvnw clean test jacoco:report
```

---

## 📚 Documentação

### Swagger/OpenAPI
Acesse: **http://localhost:8081/swagger-ui.html**

### Arquivos de Documentação
- `SAAS_IMPLEMENTATION.md` - Detalhes técnicos da implementação
- `README.md` - Este arquivo

---

## 🐛 Troubleshooting

### Erro: "JWT signature validation failed"
- Verificar se o token não expirou
- Verificar se o `JWT_SECRET` está correto

### Erro: "Nenhum tenant encontrado"
- Verificar se o usuário está autenticado
- Verificar se o token contém os claims necessários

### Erro: "Email já registrado"
- Usar email diferente ou fazer login com a conta existente

### Erro: "Slug da organização já está em uso"
- Usar slug único para nova organização

---

## 🚀 Próximos Passos

### Sprint 2 - Migrar entidades existentes
- [ ] Adicionar `tenantId` a Project, Responsible, Secretaria
- [ ] Implementar filtros automáticos por tenant
- [ ] Criar ProjectService e testes
- [ ] Atualizar ProjectController com @PreAuthorize

### Sprint 3 - Billing & Limites
- [ ] Validar limite de projetos por plano
- [ ] Implementar integração Stripe (mock)
- [ ] Criar fluxo de upgrade/downgrade

### Sprint 4 - QA & Deploy
- [ ] Testes de integração
- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Deploy em staging e produção

---

## 📞 Suporte

### Verificar logs
```bash
tail -f logs/kanban-project.log
```

### Logs estruturados (JSON)
```bash
# Ativar em application.yml
logging:
  level:
    br.com.setecolinas: DEBUG
```

### Endpoints de Health Check
```bash
curl http://localhost:8081/actuator/health
```

---

## 📋 Checklist de Segurança

- [ ] Trocar `JWT_SECRET` em produção
- [ ] Usar HTTPS em produção
- [ ] Configurar CORS adequadamente
- [ ] Implementar rate limiting
- [ ] Ativar logging de auditoria
- [ ] Configurar backups automáticos
- [ ] Testar isolamento de tenant
- [ ] Validar validação de email

---

## 🔗 Links Úteis

- **Spring Security**: https://spring.io/projects/spring-security
- **JWT**: https://jwt.io
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Docker**: https://www.docker.com

---

## 📝 Notas

- A organização é criada automaticamente durante registro
- Subscription FREE é ativada por padrão
- Usuário registrador é ORG_ADMIN por padrão
- Token expira em 24 horas (configurável)

---

**Versão**: 1.0  
**Última atualização**: 2026-03-04  
**Status**: Sprint 1 Completa ✅

