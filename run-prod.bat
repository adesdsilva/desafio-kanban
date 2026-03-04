@echo off
REM Script para rodar o Kanban SaaS em PROD

echo ========================================
echo  KANBAN SAAS - PRODUCAO (PROD)
echo ========================================
echo.
echo ATENCAO: Este script roda a aplicacao em PRODUCAO!
echo Certifique-se de que:
echo  1. JWT_SECRET esta configurado (muito seguro!)
echo  2. Banco de dados esta acessivel
echo  3. Certificado SSL/TLS esta instalado
echo  4. Firewall/WAF esta ativo
echo.
pause

REM Tentar encontrar Java
for /d %%i in ("C:\Program Files\Java\*") do (
    set JAVA_HOME=%%i
    goto found
)

for /d %%i in ("C:\Program Files (x86)\Java\*") do (
    set JAVA_HOME=%%i
    goto found
)

echo ERRO: Java nao encontrado!
exit /b 1

:found
echo JAVA_HOME: %JAVA_HOME%
echo.

REM Configurar variaveis de ambiente PROD
echo Carregando variaveis de ambiente PROD...

REM Usar .env para PROD
if exist .env (
    for /f "tokens=*" %%A in (.env) do (
        if not "%%A"=="" (
            if "%%A:~0,1%" neq "#" (
                set "%%A"
            )
        )
    )
)

REM Validar variaveis obrigatorias
if not defined JWT_SECRET (
    echo ERRO: JWT_SECRET nao configurado!
    echo Configure como variavel de ambiente
    pause
    exit /b 1
)

if not defined DB_HOST (
    echo ERRO: DB_HOST nao configurado!
    echo Configure como variavel de ambiente
    pause
    exit /b 1
)

if not defined DB_USERNAME (
    echo ERRO: DB_USERNAME nao configurado!
    echo Configure como variavel de ambiente
    pause
    exit /b 1
)

if not defined DB_PASSWORD (
    echo ERRO: DB_PASSWORD nao configurado!
    echo Configure como variavel de ambiente
    pause
    exit /b 1
)

echo.
echo ========================================
echo  INICIANDO KANBAN SAAS EM PRODUCAO
echo ========================================
echo  API URL: https://api.kanban-saas.com
echo  Banco: %DB_HOST%
echo  Usuario: %DB_USERNAME%
echo  Perfil: PROD
echo ========================================
echo.

REM Build JAR primeiro
echo Building JAR file...
call mvnw clean package -DskipTests -Dspring.profiles.active=prod

if %ERRORLEVEL% neq 0 (
    echo ERRO: Build falhou!
    pause
    exit /b 1
)

echo.
echo Iniciando aplicacao...
echo.

REM Rodar JAR com perfil PROD
java -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 ^
  -Dspring.profiles.active=prod ^
  -jar target/kanban-project-0.0.1-SNAPSHOT.jar

pause

