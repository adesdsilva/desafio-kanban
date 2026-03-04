# ✅ Ambientes Configurados com Sucesso!

## 🎉 Resumo: 4 Ambientes Profissionais Criados

Seu projeto Kanban SaaS agora tem configuração profissional com 4 ambientes totalmente isolados:

---

## 📊 Ambientes Criados

### 1️⃣ DEV (Desenvolvimento Local) 🔵

**Características:**
- ✅ Banco PostgreSQL local
- ✅ SQL queries visíveis
- ✅ Logging em DEBUG
- ✅ Swagger habilitado
- ✅ ddl-auto: update (cria tabelas)
- ✅ CORS liberal

**Como usar:**
```bash
./mvnw spring-boot:run
# Ou explicitamente:
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

**URL:**
```
API:     http://localhost:8081/api
Swagger: http://localhost:8081/swagger-ui.html
Health:  http://localhost:8081/actuator/health
```

**Banco de Dados:**
```bash
createdb -U postgres kanban_dev
```

---

### 2️⃣ HOMOL (Homologação/Staging) 🟡

**Características:**
- ✅ Banco em servidor remoto
- ✅ Logging em INFO
- ✅ SQL queries NÃO visíveis
- ✅ ddl-auto: validate (apenas valida)
- ✅ Requer variáveis de ambiente
- ✅ CORS restrito
- ✅ Ideal para QA testar

**Como usar:**
```bash
export DB_HOST="homol-db.example.com"
export DB_USERNAME="kanban_user"
export DB_PASSWORD="seu_password"
export JWT_SECRET="seu_secret_32_chars"

./mvnw spring-boot:run -Dspring.profiles.active=homol
```

**URL:**
```
API:     https://api-homol.kanban-saas.com/api
Swagger: https://api-homol.kanban-saas.com/swagger-ui.html
Logs:    logs/kanban-homol.log
```

---

### 3️⃣ PROD (Produção) 🔴

**Características:**
- ✅ Banco em servidor seguro
- ✅ Logging em WARN (apenas erros)
- ✅ SQL queries NÃO visíveis
- ✅ ddl-auto: validate (NUNCA alterar)
- ✅ TODAS variáveis de ambiente obrigatórias
- ✅ Compressão HTTP
- ✅ HTTP/2
- ✅ Batch processing
- ✅ Performance otimizado

**Como usar:**
```bash
export JWT_SECRET="seu_secret_muito_seguro_32_chars"
export DB_HOST="prod-db.example.com"
export DB_USERNAME="kanban_prod_user"
export DB_PASSWORD="sua_senha_super_segura"

java -jar kanban-project-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**URL:**
```
API:     https://api.kanban-saas.com/api
Health:  https://api.kanban-saas.com/actuator/health
Logs:    logs/kanban-prod.log
```

---

### 4️⃣ DOCKER (Container) 🐳

**Características:**
- ✅ Usa Docker container
- ✅ Banco em container separado
- ✅ Logging em DEBUG
- ✅ Ideal para desenvolvimento em container
- ✅ docker-compose.yml

**Como usar:**
```bash
docker-compose up
```

**URL:**
```
API:     http://localhost:8080/api
Swagger: http://localhost:8080/swagger-ui.html
```

---

## 📁 Arquivos Criados/Modificados

### ✅ Criados

1. **application.yml** (modificado)
   - Configuração base + 4 perfis

2. **application-homol.yml** (novo)
   - Configurações específicas HOMOL

3. **application-prod.yml** (novo)
   - Configurações específicas PROD

4. **.env.template** (novo)
   - Template de variáveis de ambiente

5. **ENVIRONMENTS_GUIDE.md** (novo)
   - Guia completo de ambientes

---

## 🔐 Variáveis de Ambiente

### Template criado: `.env.template`

```bash
# Copie para .env:
cp .env.template .env

# Preencha com valores reais
nano .env

# Use em suas shells:
export $(cat .env | grep "^PROD_" | xargs)
```

---

## 🚀 Quick Start por Ambiente

### DEV (Local)
```bash
# Criar banco
createdb -U postgres kanban_dev

# Rodar
./mvnw spring-boot:run

# Testar
curl http://localhost:8081/api/auth/login
```

### HOMOL (Staging)
```bash
# Setup
export JWT_SECRET="homol-secret-32-chars"
export DB_HOST="homol-db.com"
export DB_USERNAME="user"
export DB_PASSWORD="pass"

# Rodar
./mvnw spring-boot:run -Dspring.profiles.active=homol
```

### PROD (Produção)
```bash
# Setup
export JWT_SECRET="prod-secret-super-seguro-32-chars"
export DB_HOST="prod-db.com"
export DB_USERNAME="prod_user"
export DB_PASSWORD="senha_super_segura"

# Build
./mvnw clean package -DskipTests -Dspring.profiles.active=prod

# Rodar
java -jar target/kanban-project-*.jar --spring.profiles.active=prod
```

### DOCKER
```bash
# Rodar
docker-compose up

# Stop
docker-compose down
```

---

## 📊 Comparação Rápida

```
┌──────────────────┬───────────┬──────────────┬──────────────┬──────────┐
│ Aspecto          │ DEV       │ HOMOL        │ PROD         │ DOCKER   │
├──────────────────┼───────────┼──────────────┼──────────────┼──────────┤
│ Banco Local      │ Sim       │ Não          │ Não          │ Sim      │
│ SQL Debug        │ Sim       │ Não          │ Não          │ Sim      │
│ Log Level        │ DEBUG     │ INFO         │ WARN         │ DEBUG    │
│ ddl-auto         │ update    │ validate     │ validate     │ update   │
│ HTTPS            │ Não       │ Sim          │ Sim (obrig)  │ Não      │
│ Rate Limit       │ Não       │ Sim          │ Sim          │ Não      │
│ Compression      │ Não       │ Não          │ Sim          │ Não      │
│ Performance      │ Básico    │ Médio        │ Otimizado    │ Básico   │
│ Backup           │ Manual    │ Diário       │ Horário      │ Manual   │
│ Monitoring       │ Básico    │ Sim          │ Completo     │ Básico   │
└──────────────────┴───────────┴──────────────┴──────────────┴──────────┘
```

---

## ✅ Checklist: Ambiente Pronto?

### DEV Setup
- [ ] PostgreSQL instalado
- [ ] Database `kanban_dev` criado
- [ ] App roda em http://localhost:8081
- [ ] Swagger acessível
- [ ] Banco de dados atualizado com `ddl-auto: update`

### HOMOL Setup
- [ ] Server preparado
- [ ] Database `kanban_homol` criado
- [ ] Variáveis de ambiente configuradas
- [ ] App roda em https://api-homol.kanban-saas.com
- [ ] Certificado SSL/TLS válido
- [ ] Logs configurados

### PROD Setup
- [ ] Server de produção preparado
- [ ] Database `kanban_prod` criado em servidor seguro
- [ ] TODAS variáveis de ambiente obrigatórias configuradas
- [ ] JWT_SECRET muito seguro (32+ chars, aleatório)
- [ ] HTTPS/TLS obrigatório
- [ ] WAF/Firewall configurado
- [ ] Backup automático ativo
- [ ] Monitoring/Alertas ativos
- [ ] App roda em https://api.kanban-saas.com

### DOCKER Setup
- [ ] Docker instalado
- [ ] docker-compose.yml presente
- [ ] Volumes configurados
- [ ] App roda em http://localhost:8080
- [ ] Database acessível via `db` hostname

---

## 🔒 Security Best Practices

### DEV
- ✅ Pode usar senha simples (admin/admin)
- ✅ JWT simples é OK
- ✅ Localhost é seguro

### HOMOL
- ✅ Usar senha forte
- ✅ JWT_SECRET mínimo 32 chars
- ✅ HTTPS obrigatório
- ✅ Backup diário

### PROD
- ⚠️ JWT_SECRET MUITO seguro (use `openssl rand -base64 32`)
- ⚠️ DB_PASSWORD forte (12+ chars, mix, special chars)
- ⚠️ HTTPS/TLS obrigatório
- ⚠️ Firewall/WAF ativo
- ⚠️ Backup horário
- ⚠️ Monitoring 24/7
- ⚠️ Logs centralizados

### Gerar Secrets Seguros

```bash
# Linux/Mac
openssl rand -base64 32

# Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Java
java -c "System.out.println(java.util.UUID.randomUUID())"
```

---

## 📝 Próximos Passos

1. **Setup DEV Local**
   ```bash
   createdb kanban_dev
   ./mvnw spring-boot:run
   ```

2. **Testar Endpoints**
   ```bash
   curl -X POST http://localhost:8081/api/auth/register ...
   ```

3. **Preparar HOMOL**
   - Provisionar servidor
   - Criar database
   - Configurar HTTPS

4. **Preparar PROD**
   - Provisionar servidor
   - Configurar WAF/Firewall
   - Setup backup automático
   - Configurar monitoring

5. **CI/CD**
   - GitHub Actions para DEV
   - Testes automáticos
   - Deploy automático em HOMOL
   - Deploy manual em PROD

---

## 🎓 Exemplo: Deployment Flow

```
DEV (Desenvolvimento Local)
  ↓ (git push)
HOMOL (Teste/QA)
  ↓ (aprovação manual)
PROD (Produção)
```

### Automação com GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy
on: [push]

jobs:
  dev:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build
        run: ./mvnw clean package
      - name: Deploy DEV
        run: ./deploy-dev.sh

  homol:
    needs: dev
    if: github.branch == 'main'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy HOMOL
        run: ./deploy-homol.sh

  # PROD deploy manual
```

---

## 🆘 Troubleshooting

### Erro: "Could not connect to database"

**DEV:**
```bash
psql -U postgres -d kanban_dev -c "SELECT 1"
```

**HOMOL/PROD:**
```bash
echo $DB_HOST
echo $DB_USERNAME
pg_isready -h $DB_HOST -p 5432
```

### Erro: "Invalid JWT"

```bash
# Verificar length
echo $JWT_SECRET | wc -c  # Deve ser >= 32

# Gerar novo
openssl rand -base64 32
```

### Erro: "ddl-auto: update not working"

```bash
# Verificar config ativa
./mvnw spring-boot:run -Dspring.profiles.active=dev
# Deve estar usando: ddl-auto: update
```

---

## 📊 Estrutura de Arquivos

```
kanban-project/
├── src/main/resources/
│   ├── application.yml              (config base + 4 perfis)
│   ├── application-homol.yml        (novo - HOMOL específico)
│   ├── application-prod.yml         (novo - PROD específico)
│   ├── logback-spring.xml           (logging)
│   └── ...
├── .env.template                    (novo - template env vars)
├── ENVIRONMENTS_GUIDE.md            (novo - guia completo)
└── ...
```

---

## 🎉 Conclusão

✅ Seus 4 ambientes estão configurados profissionalmente!

**Status:**
- DEV: 🟢 Pronto para usar
- HOMOL: 🟡 Pronto para provisionar
- PROD: 🔴 Pronto para deploy
- DOCKER: 🐳 Pronto para usar

**Próximo passo:** Setup DEV local e testar endpoints!

---

**Data**: 2026-03-04  
**Status**: ✅ AMBIENTES CONFIGURADOS  
**Versão**: 1.0

