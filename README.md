# 🗂️ Desafio Kanban - API

API desenvolvida em **Spring Boot** para gerenciamento de tarefas em estilo Kanban.  
O projeto foi estruturado com foco em **boas práticas de arquitetura, segurança, observabilidade e performance**.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
  - Spring Web (REST API)
  - Spring Data JPA
  - Spring Security (JWT)
  - Spring Cache
  - Spring Boot Actuator
- **Spring GraphQL**
- **Springdoc OpenAPI (Swagger)**
- **Prometheus (monitoramento)**
- **PostgreSQL (banco de dados)**
- **Docker / Docker Compose**
- **JUnit + Mockito (testes unitários e de integração)**

---

## ⚙️ Funcionalidades Principais

- ✅ **API Dockerizada**
  - Porta **8080** para execução via Docker
  - Porta **8081** para execução local (IDE)
- ✅ **Endpoints RESTful** (CRUD completo)
- ✅ **GraphQL** para consultas avançadas
- ✅ **Paginação em listagens** para performance
- ✅ **Cache** para otimização de queries
- ✅ **Filtros dinâmicos** para entidades
- ✅ **Auditoria mínima**: `createdAt`, `updatedAt`
- ✅ **Validação de inputs** (Bean Validation)
- ✅ **Tratamento centralizado de erros**
- ✅ **Logs estruturados e personalizados**
- ✅ **Clean Code & SOLID**
- ✅ **Testes automatizados**
  - Camada Controller
  - Camada Service
- ✅ **Segurança**
  - Autenticação via **JWT**
  - Autorização via **Spring Security**
- ✅ **Observabilidade**
  - Métricas com **Prometheus**
  - Health check e monitoramento com **Actuator**

---

## 📊 Observabilidade & Monitoramento

### 🔹 Actuator Endpoints
A aplicação expõe endpoints para monitoramento e gestão:

- `/actuator/health` → Status da aplicação  
- `/actuator/metrics` → Métricas gerais  
- `/actuator/prometheus` → Exporta métricas no formato Prometheus  
- `/actuator/loggers` → Configuração de log em tempo real  
- `/actuator/env` → Variáveis de ambiente  
- `/actuator/beans` → Beans registrados  
- `/actuator/threaddump` → Dump de threads  
- `/actuator/httptrace` → Últimas requisições HTTP  

### 🔹 Prometheus
As métricas expostas em `/actuator/prometheus` podem ser consumidas pelo **Prometheus** para dashboards e alertas.

---

## 🔒 Segurança

- Autenticação baseada em **JWT**
- Proteção de endpoints REST
- Configuração integrada ao Swagger/OpenAPI, permitindo uso do botão **Authorize**

---

## 📖 Documentação (Swagger / OpenAPI)

- Swagger UI disponível em:
  - `http://localhost:8081/swagger-ui-custom.html` (IDE)
  - `http://localhost:8080/swagger-ui-custom.html` (Docker)

---

## 🐳 Execução com Docker

### Pré-requisitos
- Docker
- Docker Compose

### Comandos
```bash
# Build da aplicação
mvn clean package -DskipTests

Aplicação disponível em:
👉 http://localhost:8080

# Executar com Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

🧪 Testes

Os testes cobrem:

 - Controllers

 - Services

 - Regras de negócio

Para rodar:

mvn test

🗄️ Banco de Dados

 - PostgreSQL

Configurado via docker-compose

Scripts de inicialização automáticos

📌 Estrutura de Código:
src/main/java/com/example/kanban
├── config        # Configurações (Swagger, Security, Cache, etc.)
├── controller    # Endpoints REST e GraphQL
├── dto           # Objetos de transporte
├── entity        # Entidades JPA
├── repository    # Repositórios JPA
├── service       # Regras de negócio
└── util          # Utilitários e helpers

👨‍💻 Autor
Projeto desenvolvido por Adelino Silva



# Subir aplicação e banco Postgres
docker-compose up -d
