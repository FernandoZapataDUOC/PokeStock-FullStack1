@echo off
echo.
echo =======================================================================
echo Iniciando Sistema PokeStock...
echo =======================================================================
echo.

echo [1/3] Iniciando Eureka Server...
start "Eureka Server" java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar
echo Esperando 15 segundos a que Eureka Server levante por completo...
timeout /t 15 /nobreak

echo.
echo [2/3] Iniciando Microservicios de Negocio y Seguridad en paralelo...
start "ms-auth" java -jar Seguridad\ms-auth\target\ms-auth-0.0.1-SNAPSHOT.jar
start "ms-security" java -jar Seguridad\ms-security\target\ms-security-0.0.1-SNAPSHOT.jar
start "ms-usuarios" java -jar Seguridad\ms-usuarios\target\ms-usuarios-0.0.1-SNAPSHOT.jar
start "ms-productos" java -jar ms-productos\target\ms-productos-0.0.1-SNAPSHOT.jar
start "ms-proveedores" java -jar ms-proveedores\target\ms-proveedores-0.0.1-SNAPSHOT.jar
start "ms-movimientos" java -jar ms-movimientos\target\ms-movimientos-0.0.1-SNAPSHOT.jar
start "ms-documentos" java -jar ms-documentos\target\ms-documentos-0.0.1-SNAPSHOT.jar
start "ms-stock" java -jar ms-stock\target\ms-stock-0.0.1-SNAPSHOT.jar
start "ms-validaciones" java -jar ms-validaciones\target\ms-validaciones-0.0.1-SNAPSHOT.jar
start "ms-reportes" java -jar ms-reportes\target\ms-reportes-0.0.1-SNAPSHOT.jar

echo Esperando 20 segundos a que los microservicios se registren en Eureka...
timeout /t 20 /nobreak

echo.
echo [3/3] Iniciando API Gateway...
start "API Gateway" java -jar api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar

echo.
echo =======================================================================
echo Sistema PokeStock iniciado.
echo URLs principales:
echo   - Eureka Dashboard: http://localhost:8761
echo   - API Gateway:      http://localhost:8080
echo =======================================================================
echo.
pause
