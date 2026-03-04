@echo off
REM ========================================
REM Kanban SaaS - Setup Completo e Execução
REM ========================================

setlocal enabledelayedexpansion

echo.
echo ========================================
echo  KANBAN SAAS - SETUP COMPLETO
echo ========================================
echo.

REM ========================================
REM PASSO 1: Encontrar Java
REM ========================================
echo [1/5] Procurando Java...

set "JAVA_FOUND=0"

REM Opção 1: C:\Program Files\Java
for /d %%A in ("C:\Program Files\Java\jdk*") do (
    set "JAVA_HOME=%%A"
    set "JAVA_FOUND=1"
    goto :java_found
)

REM Opção 2: C:\Program Files (x86)\Java
for /d %%A in ("C:\Program Files (x86)\Java\jdk*") do (
    set "JAVA_HOME=%%A"
    set "JAVA_FOUND=1"
    goto :java_found
)

REM Opção 3: C:\Java
for /d %%A in ("c:\java\jdk*") do (
    set "JAVA_HOME=%%A"
    set "JAVA_FOUND=1"
    goto :java_found
)

if !JAVA_FOUND! equ 0 (
    echo [ERRO] Java não encontrado!
    echo.
    echo Instale Java 17+: https://adoptium.net/
    pause
    exit /b 1
)

:java_found
echo [OK] Java encontrado: !JAVA_HOME!

REM ========================================
REM PASSO 2: Criar Banco de Dados
REM ========================================
echo.
echo [2/5] Verificando banco de dados PostgreSQL...

where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo [AVISO] PostgreSQL não encontrado em PATH
    echo.
    echo Por favor, execute manualmente:
    echo   createdb -U postgres kanban_dev
    echo.
    echo Ou crie via pgAdmin/DBeaver
    echo.
    echo Pressione uma tecla para continuar...
    pause >nul
) else (
    echo [CHECK] Verificando se banco kanban_dev existe...
    psql -U postgres -lqt 2>nul | find "kanban_dev" >nul
    if %errorlevel% neq 0 (
        echo [CRIAR] Banco kanban_dev não existe. Criando...
        psql -U postgres -c "CREATE DATABASE kanban_dev;" 2>nul
        if %errorlevel% equ 0 (
            echo [OK] Banco kanban_dev criado com sucesso
        ) else (
            echo [ERRO] Falha ao criar banco
            echo Verifique se PostgreSQL está rodando e credenciais postgres estão corretas
            pause
            exit /b 1
        )
    ) else (
        echo [OK] Banco kanban_dev já existe
    )
)

REM ========================================
REM PASSO 3: Compilar Projeto
REM ========================================
echo.
echo [3/5] Compilando projeto...

set "PATH=!JAVA_HOME!\bin;!PATH!"

call mvnw.cmd clean compile -DskipTests -q >nul 2>&1

if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilação
    pause
    exit /b 1
)

echo [OK] Projeto compilado com sucesso

REM ========================================
REM PASSO 4: Informações
REM ========================================
echo.
echo [4/5] Informações do ambiente:
echo   JAVA_HOME: !JAVA_HOME!
echo   Java:
"!JAVA_HOME!\bin\java.exe" -version 2>&1 | findstr /r "version"

REM ========================================
REM PASSO 5: Rodar Aplicação
REM ========================================
echo.
echo [5/5] Iniciando aplicação...
echo.
echo =========================================
echo  KANBAN SAAS - RODANDO
echo =========================================
echo.
echo API:      http://localhost:8081/api
echo Swagger:  http://localhost:8081/swagger-ui.html
echo Health:   http://localhost:8081/actuator/health
echo Metrics:  http://localhost:8081/actuator/prometheus
echo.
echo Aguarde a inicialização (pode levar 10-15 segundos)...
echo.

call mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha ao iniciar aplicação
    pause
    exit /b 1
)

pause

