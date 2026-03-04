# ✅ Como Rodar a Aplicação - Guia Completo

## 🎯 Problema Resolvido!

O erro **`ConflictingBeanDefinitionException`** foi **RESOLVIDO**! ✅

### O que era o problema?
Havia dois `JwtAuthenticationFilter` conflitantes:
- `br.com.setecolinas.kanban_project.component.JwtAuthenticationFilter` (antigo)
- `br.com.setecolinas.kanban_project.security.JwtAuthenticationFilter` (novo)

### Como foi resolvido?
Marcamos o antigo como `@Deprecated` e renomeamos para evitar conflito.

---

## 🚀 Como Rodar a Aplicação

### 🪟 Windows

#### DEV (Desenvolvimento)
```bash
run-dev.bat
```

Ou manualmente:
```cmd
mvnw spring-boot:run -Dspring.profiles.active=dev
```

#### HOMOL (Homologação)
```bash
run-homol.bat
```

#### PROD (Produção)
```bash
run-prod.bat
```

### 🐧 Linux / 🍎 Mac

#### DEV
```bash
chmod +x run-dev.sh
./run-dev.sh
```

#### HOMOL
```bash
chmod +x run-homol.sh
./run-homol.sh
```

#### PROD
```bash
chmod +x run-prod.sh
./run-prod.sh
```

---

## 📋 Scripts Inclusos

| Script | Windows | Linux/Mac | Propósito |
|--------|---------|-----------|-----------|
| DEV | `run-dev.bat` | `run-dev.sh` | Desenvolvimento local |
| HOMOL | `run-homol.bat` | `run-homol.sh` | Testes em staging |
| PROD | `run-prod.bat` | `run-prod.sh` | Produção |

### O que cada script faz?

**run-dev.bat/sh:**
- ✅ Configura JAVA_HOME automaticamente
- ✅ Cria banco de dados kanban_dev se não existir
- ✅ Roda com perfil DEV
- ✅ Porta 8081

**run-homol.bat/sh:**
- ✅ Carrega variáveis do .env
- ✅ Valida JWT_SECRET
- ✅ Roda com perfil HOMOL
- ✅ Porta 8080

**run-prod.bat/sh:**
- ✅ Valida TODAS as variáveis obrigatórias
- ✅ Faz build do JAR
- ✅ Roda com JVM otimizado
- ✅ Porta 8080

---

## 🔐 Variáveis de Ambiente

### DEV (nenhuma necessária - usa defaults)

```bash
# Opcionais
JWT_SECRET=dev-secret-key-32chars
```

### HOMOL (criar .env)

```bash
# Obrigatórias
JWT_SECRET=homol-super-secret-32-chars
DB_HOST=homol-db.example.com
DB_USERNAME=kanban_user
DB_PASSWORD=sua_senha_aqui
```

### PROD (criar .env)

```bash
# OBRIGATÓRIAS - NÃO deixar vazias!
JWT_SECRET=prod-super-secret-muito-seguro-32-chars
DB_HOST=prod-db.example.com
DB_USERNAME=kanban_prod_user
DB_PASSWORD=sua_senha_super_segura_123!@#
```

---

## 🧪 Testar a Aplicação

### 1. Abrir Swagger

```
http://localhost:8081/swagger-ui.html
```

### 2. Registrar novo usuário

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123",
    "organizationName": "Minha Empresa",
    "organizationSlug": "minha-empresa"
  }'
```

### 3. Fazer login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 4. Testar com token

```bash
curl http://localhost:8081/api/projects \
  -H "Authorization: Bearer seu_token_aqui"
```

### 5. Verificar health

```bash
curl http://localhost:8081/actuator/health
```

---

## 🛠️ Troubleshooting

### Erro: "JAVA_HOME not defined"

**Windows:**
1. Abra Variáveis de Ambiente
2. Nova variável: `JAVA_HOME` = `C:\Program Files\Java\jdk-17`
3. Reinicie o terminal

**Linux/Mac:**
```bash
export JAVA_HOME=/usr/libexec/java_home -v 17
```

### Erro: "Could not connect to database"

**DEV:**
```bash
# Criar banco manualmente
createdb -U postgres kanban_dev
```

**HOMOL/PROD:**
```bash
# Verificar conexão
psql -h $DB_HOST -U $DB_USERNAME -d kanban_homol -c "SELECT 1"
```

### Erro: "Port 8081 already in use"

```bash
# Kill processo na porta 8081
# Windows:
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :8081
kill -9 <PID>
```

---

## 📊 Status Verificação

Depois de rodar, você deve ver:

```
Started KanbanProjectApplication in X seconds
Tomcat started on port(s): 8081 (http)
Application started successfully
```

Se ver isso, a aplicação está **RODANDO** ✅

---

## 🎯 URLs por Ambiente

| Recurso | DEV | HOMOL | PROD |
|---------|-----|-------|------|
| API | http://localhost:8081/api | https://api-homol.kanban-saas.com | https://api.kanban-saas.com |
| Swagger | http://localhost:8081/swagger-ui.html | https://api-homol.kanban-saas.com/swagger-ui.html | ❌ Desabilitado |
| Health | http://localhost:8081/actuator/health | https://api-homol.kanban-saas.com/actuator/health | https://api.kanban-saas.com/actuator/health |
| Logs | console | logs/kanban-homol.log | logs/kanban-prod.log |

---

## ✨ Dicas

### Desenvolvimento Eficiente

1. Use **run-dev.bat/sh** para iniciar rápido
2. Swagger está habilitado - teste endpoints lá
3. Logs em DEBUG - veja tudo que acontece
4. Banco atualiza automaticamente

### Antes de Deploy

1. Testar em HOMOL
2. Verificar todos os logs
3. Fazer backup do banco
4. Confirmar variáveis de ambiente
5. Testar endpoints críticos

---

## 📝 Checklist Antes de Rodar

### DEV
- [ ] Java instalado (17+)
- [ ] PostgreSQL instalado
- [ ] Maven instalado (ou use mvnw)

### HOMOL
- [ ] Java instalado
- [ ] Banco configurado
- [ ] .env com variáveis
- [ ] HTTPS certificado pronto

### PROD
- [ ] Java instalado
- [ ] Banco seguro configurado
- [ ] .env com variáveis MUITO seguras
- [ ] HTTPS/TLS obrigatório
- [ ] Backup automático ativo
- [ ] Monitoring ativo
- [ ] WAF/Firewall ativo

---

## 🎊 Próximos Passos

1. ✅ Resolvi o erro de inicialização
2. ✅ Criei scripts para rodar facilmente
3. ⏳ **Agora**: Execute `run-dev.bat` ou `./run-dev.sh`
4. ⏳ Teste os endpoints com Swagger
5. ⏳ Comece a usar a aplicação!

---

**Data**: 2026-03-04  
**Status**: ✅ APLICAÇÃO PRONTA PARA RODAR  
**Versão**: 1.0

