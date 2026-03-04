#!/bin/bash
# Script para rodar Kanban SaaS em HOMOL (Linux/Mac)

set -e

echo "========================================"
echo "  KANBAN SAAS - HOMOL ENVIRONMENT"
echo "========================================"
echo ""

# Encontrar Java
if [ -x "$(command -v java)" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "JAVA_HOME: $JAVA_HOME"
else
    echo "ERRO: Java não encontrado!"
    exit 1
fi

echo "Versão de Java:"
java -version
echo ""

# Carregar variáveis de ambiente de .env
if [ -f ".env" ]; then
    echo "Carregando variáveis de .env..."
    export $(cat .env | grep "^HOMOL_" | xargs)
fi

# Validar variáveis obrigatórias
if [ -z "$JWT_SECRET" ]; then
    echo "ERRO: JWT_SECRET não configurado!"
    echo "Configure em .env ou como variável de ambiente"
    exit 1
fi

DB_HOST=${DB_HOST:-homol-db.example.com}

echo ""
echo "Iniciando Kanban SaaS em HOMOL..."
echo "  API: https://api-homol.kanban-saas.com"
echo "  Banco: $DB_HOST"
echo ""

# Rodar com perfil HOMOL
./mvnw spring-boot:run -Dspring.profiles.active=homol

