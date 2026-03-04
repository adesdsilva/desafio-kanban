#!/bin/bash
# Script para rodar Kanban SaaS em DEV (Linux/Mac)

set -e

echo "========================================"
echo "  KANBAN SAAS - DEV ENVIRONMENT"
echo "========================================"
echo ""

# Encontrar Java
if [ -x "$(command -v java)" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "JAVA_HOME: $JAVA_HOME"
else
    echo "ERRO: Java não encontrado!"
    echo "Por favor, instale Java 17+ ou configure JAVA_HOME"
    exit 1
fi

# Mostrar versão de Java
echo "Versão de Java:"
java -version
echo ""

# Criar banco de dados DEV se não existir
echo "Verificando banco de dados DEV..."
if command -v psql &> /dev/null; then
    psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'kanban_dev'" | grep -q 1 || \
        psql -U postgres -c "CREATE DATABASE kanban_dev"
    echo "✅ Banco kanban_dev OK"
else
    echo "⚠️  PostgreSQL não encontrado. Execute manualmente:"
    echo "    createdb -U postgres kanban_dev"
fi
echo ""

echo "Iniciando aplicação Kanban SaaS em DEV..."
echo "  Porta: 8081"
echo "  Swagger: http://localhost:8081/swagger-ui.html"
echo ""

# Rodar com perfil DEV
./mvnw spring-boot:run -Dspring.profiles.active=dev

