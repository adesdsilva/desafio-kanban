@echo off
REM Script para rodar o Kanban SaaS em HOMOL

echo ========================================
echo  KANBAN SAAS - HOMOL ENVIRONMENT
echo ========================================
echo.

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
echo Versao de Java:
%JAVA_HOME%\bin\java -version
echo.

REM Configurar variaveis de ambiente HOMOL
echo Configurando variaveis de ambiente HOMOL...
echo.

REM Usar .env para HOMOL (se existir)
if exist .env (
    echo Carregando variaveis de .env...
    for /f "tokens=*" %%A in (.env) do (
        if not "%%A"=="" (
            if "%%A:~0,1%" neq "#" (
                set "%%A"
            )
        )
    )
)

REM Valores padroes se nao configurado
if not defined JWT_SECRET (
    echo ERRO: JWT_SECRET nao configurado!
    echo Por favor, configure em .env ou como variavel de ambiente
    pause
    exit /b 1
)

if not defined DB_HOST (
    set DB_HOST=homol-db.example.com
)

echo.
echo Iniciando Kanban SaaS em HOMOL...
echo  API: https://api-homol.kanban-saas.com
echo  Banco: %DB_HOST%
echo.

REM Rodar com perfil HOMOL
call mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=homol"

pause

