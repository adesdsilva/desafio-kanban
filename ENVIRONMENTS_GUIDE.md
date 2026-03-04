# 🌍 Configuração de Ambientes - Kanban SaaS

## 📋 Resumo de Ambientes

O projeto suporta 4 ambientes principais:

| Ambiente | Perfil | DB | Port | Uso | Log Level |
|----------|--------|----|----|-----|-----------|
| **DEV** | dev | kanban_dev | 8081 | Desenvolvimento local | DEBUG |
| **HOMOL** | homol | kanban_homol | 8080 | Testes antes de prod | INFO |
| **PROD** | prod | kanban_prod | 8080 | Produção | WARN |
| **DOCKER** | docker | kanban_docker | 8080 | Container | DEBUG |

---

## 🔵 AMBIENTE DEV (Desenvolvimento)

### Características
- ✅ Banco de dados local
- ✅ Logging em DEBUG
- ✅ SQL queries mostradas
- ✅ Swagger habilitado
- ✅ CORS liberal (localhost)
- ✅ ddl-auto: update (cria/atualiza tabelas)
- ✅ JWT simples (para desenvolvimento)

### Como usar

```bash
# Padrão (já ativado no application.yml)
./mvnw spring-boot:run

# Ou explicitamente
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Variáveis de Ambiente (Opcional)

```bash
# As variáveis abaixo são opcionais. Se não setadas, usam defaults

export JWT_SECRET="dev-secret-key-only-for-development-32chars"
export JWT_EXPIRATION="86400000"
export APP_ENV="dev"
```

### Banco de Dados

```bash
# Criar banco de dados local
createdb -U postgres kanban_dev

# Ou via psql
psql -U postgres -c "CREATE DATABASE kanban_dev;"
```

### URLs Locais

```
API:      http://localhost:8081/api
Swagger:  http://localhost:8081/swagger-ui.html
Health:   http://localhost:8081/actuator/health
Metrics:  http://localhost:8081/actuator/prometheus
```

---

## 🟡 AMBIENTE HOMOL (Homologação/Staging)

### Características
- ✅ Banco de dados em servidor
- ✅ Logging em INFO (menos verboso)
- ✅ SQL queries NÃO mostradas
- ✅ ddl-auto: validate (apenas valida schema)
- ✅ Requer variáveis de ambiente
- ✅ CORS restrito
- ✅ Ideal para QA testar

### Como usar

```bash
./mvnw spring-boot:run -Dspring.profiles.active=homol
```

### Variáveis de Ambiente (OBRIGATÓRIAS)

```bash
export JWT_SECRET="homol-super-secret-key-at-least-32-chars-long"
export JWT_EXPIRATION="86400000"

# Banco de dados
export DB_HOST="homol-db.example.com"
export DB_USERNAME="kanban_user"
export DB_PASSWORD="secure_password_123"

# App
export APP_ENV="homol"
```

### URLs

```
API:      https://api-homol.kanban-saas.com/api
Swagger:  https://api-homol.kanban-saas.com/swagger-ui.html
Health:   https://api-homol.kanban-saas.com/actuator/health
Logs:     logs/kanban-homol.log
```

### Backup de Banco

```bash
# Backup
pg_dump -h homol-db.example.com -U kanban_user kanban_homol > backup_homol.sql

# Restore
psql -h homol-db.example.com -U kanban_user kanban_homol < backup_homol.sql
```

---

## 🔴 AMBIENTE PROD (Produção)

### Características
- ✅ Banco de dados em servidor
- ✅ Logging em WARN (apenas erros)
- ✅ SQL queries NÃO mostradas
- ✅ ddl-auto: validate (NUNCA alterar schema)
- ✅ Requer TODAS variáveis de ambiente
- ✅ CORS restrito
- ✅ Compressão HTTP habilitada
- ✅ HTTP/2 habilitado
- ✅ Conexão pool otimizado
- ✅ Batch processing habilitado

### Como usar

```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Variáveis de Ambiente (OBRIGATÓRIAS)

```bash
# ⚠️ NUNCA commitar essas variáveis no Git!

# JWT - DEVE ser muito seguro em produção
export JWT_SECRET="prod-super-secret-key-at-least-32-chars-long-use-strong-random"
export JWT_EXPIRATION="86400000"

# Banco de dados - DEVE estar em servidor seguro
export DB_HOST="prod-db.example.com"
export DB_USERNAME="kanban_prod_user"
export DB_PASSWORD="very_secure_password_123_!@#$%^&*()"

# App
export APP_ENV="prod"

# Opcional mas recomendado
export JAVA_OPTS="-Xmx2g -Xms1g"
```

### URLs

```
API:      https://api.kanban-saas.com/api
Swagger:  https://api.kanban-saas.com/swagger-ui.html (desabilitar em prod)
Health:   https://api.kanban-saas.com/actuator/health
Logs:     logs/kanban-prod.log
```

### Security Checklist

- [ ] JWT_SECRET configurado (mínimo 32 chars, aleatório)
- [ ] DB_PASSWORD forte
- [ ] HTTPS/TLS habilitado
- [ ] Swagger desabilitado (remover acesso público)
- [ ] Rate limiting ativado
- [ ] WAF (Web Application Firewall) ativado
- [ ] Backup automático de banco
- [ ] Monitoring e alertas configurados
- [ ] Logs centralizados (ELK, Datadog, etc)

### Performance em Produção

```bash
# Flags JVM recomendadas
-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Backup Automático

```bash
# Adicionar ao crontab
0 2 * * * pg_dump -h prod-db.example.com -U kanban_prod_user kanban_prod > /backups/kanban_prod_$(date +\%Y\%m\%d).sql
```

---

## 🐳 AMBIENTE DOCKER

### Características
- ✅ Usa container Docker
- ✅ Banco em container separado
- ✅ Logging em DEBUG (desenvolver em container)
- ✅ Ideal para desenvolvimento em container
- ✅ docker-compose.yml configurado

### Como usar

```bash
# Build
docker build -t kanban-saas:latest .

# Run com docker-compose
docker-compose up

# Run com docker run
docker run -p 8080:8080 \
  -e JWT_SECRET="docker-secret" \
  -e DB_HOST="db" \
  kanban-saas:latest
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - JWT_SECRET=docker-dev-secret
      - DB_HOST=db
      - DB_USERNAME=postgres
      - DB_PASSWORD=admin
    depends_on:
      - db

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=kanban_docker
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=admin
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### URLs Docker

```
API:      http://localhost:8080/api
Swagger:  http://localhost:8080/swagger-ui.html
Health:   http://localhost:8080/actuator/health
```

---

## 🔄 Migração Entre Ambientes

### DEV → HOMOL

```bash
# 1. Fazer backup do banco DEV
pg_dump -U postgres kanban_dev > backup_dev.sql

# 2. Restaurar em HOMOL
psql -h homol-db.example.com -U kanban_user kanban_homol < backup_dev.sql

# 3. Testar em HOMOL
./mvnw spring-boot:run -Dspring.profiles.active=homol
```

### HOMOL → PROD

```bash
# 1. Backup completo HOMOL
pg_dump -h homol-db.example.com -U kanban_user kanban_homol > backup_homol.sql

# 2. Restaurar em PROD
psql -h prod-db.example.com -U kanban_prod_user kanban_prod < backup_homol.sql

# 3. Validar schema
psql -h prod-db.example.com -U kanban_prod_user -c "\dt" kanban_prod

# 4. Deploy em PROD
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

---

## 📝 Arquivo .env (gitignore)

Criar arquivo `.env` na raiz do projeto (NÃO commitar):

```bash
# .env (add to .gitignore)

# DEV
DEV_JWT_SECRET=dev-secret-key-32-chars-long
DEV_DB_HOST=localhost

# HOMOL
HOMOL_JWT_SECRET=homol-secret-key-32-chars-long
HOMOL_DB_HOST=homol-db.example.com
HOMOL_DB_USER=kanban_user
HOMOL_DB_PASS=secure_password

# PROD
PROD_JWT_SECRET=prod-secret-key-32-chars-very-secure
PROD_DB_HOST=prod-db.example.com
PROD_DB_USER=kanban_prod_user
PROD_DB_PASS=very_secure_password_!@#
```

---

## 🚀 Deploy Checklist

### Antes de Deploy

- [ ] Código testado em DEV
- [ ] QA aprovou em HOMOL
- [ ] Backup de dados feito
- [ ] Variáveis de ambiente configuradas
- [ ] Certificados SSL/TLS validados
- [ ] Firewall rules configuradas
- [ ] Load balancer testado
- [ ] Monitoring/alertas ativados

### Após Deploy

- [ ] Health check passando
- [ ] Logs sem erros
- [ ] Usuarios conseguem fazer login
- [ ] Métricas normais
- [ ] Backup automático funcionando
- [ ] Notificações de erro ativadas

---

## 🔍 Monitoramento por Ambiente

### DEV
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/metrics
```

### HOMOL
```bash
curl https://api-homol.kanban-saas.com/actuator/health
curl https://api-homol.kanban-saas.com/actuator/prometheus
```

### PROD
```bash
curl https://api.kanban-saas.com/actuator/health
# Prometheus/Grafana
# ELK/Datadog/NewRelic
```

---

## 📊 Comparação Rápida

```
┌──────────────┬─────────────┬──────────────┬────────────────┐
│ Aspecto      │ DEV         │ HOMOL        │ PROD           │
├──────────────┼─────────────┼──────────────┼────────────────┤
│ DB Local     │ Sim         │ Não          │ Não            │
│ SQL Debug    │ Sim         │ Não          │ Não            │
│ Log Level    │ DEBUG       │ INFO         │ WARN           │
│ ddl-auto     │ update      │ validate     │ validate       │
│ HTTPS        │ Não         │ Sim          │ Sim (obrig)    │
│ Rate Limit   │ Não         │ Sim          │ Sim            │
│ Backup       │ Manual      │ Diário       │ Horário        │
│ Monitoring   │ Básico      │ Sim          │ Completo       │
│ Custo        │ $0          │ $$           │ $$$            │
└──────────────┴─────────────┴──────────────┴────────────────┘
```

---

## 🎓 Exemplos Práticos

### Exemplo 1: Deploy DEV Local

```bash
# 1. Criar banco
createdb -U postgres kanban_dev

# 2. Rodar app
./mvnw spring-boot:run

# 3. Testar
curl http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com",...}'
```

### Exemplo 2: Deploy HOMOL em Server

```bash
# 1. SSH no servidor
ssh user@homol-server.com

# 2. Setup variáveis
export JWT_SECRET="homol-super-secure-secret-32-chars"
export DB_HOST="homol-db.example.com"
export DB_USERNAME="kanban_user"
export DB_PASSWORD="secure_pass_123"

# 3. Build & Run
git clone repo && cd kanban-project
./mvnw clean package -Dspring.profiles.active=homol -DskipTests
java -jar target/kanban-project-*.jar --spring.profiles.active=homol
```

### Exemplo 3: Deploy PROD com Docker

```bash
# 1. Build image
docker build -t kanban-saas:v1.0.0 .

# 2. Push para registry
docker push myregistry.com/kanban-saas:v1.0.0

# 3. Deploy em K8s/Docker
kubectl apply -f k8s-deployment.yml
```

---

## 🆘 Troubleshooting

### Problema: "Could not connect to database"

```bash
# DEV: Verificar se PostgreSQL está rodando
psql -U postgres -d kanban_dev -c "SELECT 1"

# HOMOL/PROD: Verificar variáveis de ambiente
echo $DB_HOST
echo $DB_USERNAME
```

### Problema: "Invalid JWT Secret"

```bash
# Verificar secret
echo $JWT_SECRET | wc -c  # Deve ter >= 32 chars

# Gerar novo secret seguro
openssl rand -base64 32
```

### Problema: "Migration Failed"

```bash
# DEV: usar ddl-auto: update (cria tabelas)
# HOMOL/PROD: usar ddl-auto: validate (apenas valida)

# Se schema está errado:
1. Backup do banco
2. Deletar database
3. Recriar e restaurar
```

---

## 📚 Referências

- Spring Profiles: https://spring.io/projects/spring-cloud-config
- Environment Variables: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
- Logging: https://spring.io/guides/gs/logging-log4j2/

---

**Criado em**: 2026-03-04  
**Versão**: 1.0  
**Status**: ✅ PRONTO PARA USO

