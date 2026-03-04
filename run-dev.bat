@echo off
REM Script para rodar o Kanban SaaS

REM Tentar encontrar Java
for /d %%i in ("C:\Program Files\Java\*") do (
    set JAVA_HOME=%%i
    goto found
)

REM Se não encontrou em Program Files, tentar Program Files (x86)
for /d %%i in ("C:\Program Files (x86)\Java\*") do (
    set JAVA_HOME=%%i
    goto found
)

REM Se ainda não encontrou, procurar em c:\java
for /d %%i in ("c:\java\*") do (
    set JAVA_HOME=%%i
    goto found
)

echo ERRO: Java nao encontrado!
echo Por favor, instale Java 17+ ou configure JAVA_HOME manualmente
pause
exit /b 1

:found
echo JAVA_HOME configurado para: %JAVA_HOME%
echo.

REM Mostrar versão de Java
echo Versao de Java:
%JAVA_HOME%\bin\java -version
echo.

REM Rodar Maven
echo Iniciando aplicacao Kanban SaaS...
echo.
call mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

pause

