@echo off
echo.
echo =======================================================================
echo Compilando todos los modulos de PokeStock usando pom.xml padre...
echo =======================================================================
echo.
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] La compilacion ha fallado. Por favor, revisa los errores.
    pause
    exit /b %ERRORLEVEL%
)
echo.
echo =======================================================================
echo Compilacion finalizada con exito de todos los modulos.
echo =======================================================================
echo.
pause
