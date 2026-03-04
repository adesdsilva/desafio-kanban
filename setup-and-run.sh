#!/bin/bash
# Kanban SaaS - Script completo de setup e inicialização

set -e

echo ""
echo "========================================"
echo "  KANBAN SAAS - SETUP COMPLETO"
echo "========================================"
echo ""

# 1. Encontrar Java
echo "[1/4] Procurando Java..."
if [ -x "$(command -v java)" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "[OK] Java encontrado em: $JAVA_HOME"
else
    echo "[ERRO] Java não encontrado. Instale Java 17+: https://adoptium.net/"
    exit 1
fi

# 2. Criar banco de dados
echo ""
echo "[2/4] Verificando banco de dados..."
if command -v psql &> /dev/null; then
    # Verificar se banco já existe
    if psql -U postgres -lqt | cut -d \| -f 1 | grep -qw "kanban_dev"; then
        echo "[OK] Banco kanban_dev já existe"
    else
        echo "[CRIAR] Criando banco kanban_dev..."
        psql -U postgres -c "CREATE DATABASE kanban_dev;"
        echo "[OK] Banco kanban_dev criado"
    fi
else
    echo "[AVISO] PostgreSQL não encontrado"
    echo "Execute manualmente: createdb -U postgres kanban_dev"
fi

# 3. Compilar
echo ""
echo "[3/4] Compilando projeto..."
./mvnw clean compile -DskipTests -q
echo "[OK] Projeto compilado"

# 4. Rodar aplicação
echo ""
echo "[4/4] Iniciando aplicação..."
echo ""
echo "========================================="
echo "  KANBAN SAAS - RODANDO"
echo "========================================="
echo ""
echo "API:      http://localhost:8081/api"
echo "Swagger:  http://localhost:8081/swagger-ui.html"
echo "Health:   http://localhost:8081/actuator/health"
echo ""
echo "Aguarde a inicialização..."
echo ""

./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

