#!/bin/bash
# Script para rodar Kanban SaaS em PROD (Linux/Mac)

set -e

echo "========================================"
echo "  KANBAN SAAS - PRODUCAO (PROD)"
echo "========================================"
echo ""
echo "ATENCAO: Este script roda a aplicacao em PRODUCAO!"
echo "Certifique-se de que:"
echo "  1. JWT_SECRET esta configurado (muito seguro!)"
echo "  2. Banco de dados esta acessivel"
echo "  3. Certificado SSL/TLS esta instalado"
echo "  4. Firewall/WAF esta ativo"
echo ""
read -p "Pressione Enter para continuar..."

# Encontrar Java
if [ -x "$(command -v java)" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
else
    echo "ERRO: Java não encontrado!"
    exit 1
fi

# Carregar variáveis de ambiente
if [ -f ".env" ]; then
    echo "Carregando variáveis de ambiente PROD..."
    export $(cat .env | grep "^PROD_" | xargs)
fi

# Validar variáveis obrigatórias
if [ -z "$JWT_SECRET" ]; then
    echo "ERRO: JWT_SECRET não configurado!"
    exit 1
fi

if [ -z "$DB_HOST" ]; then
    echo "ERRO: DB_HOST não configurado!"
    exit 1
fi

if [ -z "$DB_USERNAME" ]; then
    echo "ERRO: DB_USERNAME não configurado!"
    exit 1
fi

if [ -z "$DB_PASSWORD" ]; then
    echo "ERRO: DB_PASSWORD não configurado!"
    exit 1
fi

echo ""
echo "========================================"
echo "  INICIANDO KANBAN SAAS EM PRODUCAO"
echo "========================================"
echo "  API URL: https://api.kanban-saas.com"
echo "  Banco: $DB_HOST"
echo "  Usuario: $DB_USERNAME"
echo "  Perfil: PROD"
echo "========================================"
echo ""

# Build JAR
echo "Building JAR file..."
./mvnw clean package -DskipTests -Dspring.profiles.active=prod

if [ $? -ne 0 ]; then
    echo "ERRO: Build falhou!"
    exit 1
fi

echo ""
echo "Iniciando aplicação..."
echo ""

# Rodar JAR com perfil PROD
java -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -Dspring.profiles.active=prod \
  -jar target/kanban-project-0.0.1-SNAPSHOT.jar

