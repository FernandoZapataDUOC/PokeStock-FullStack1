# Guía de Despliegue y Configuración de PokeStock

Esta guía describe los requisitos, la configuración inicial y el procedimiento paso a paso para levantar el sistema completo de PokeStock localmente.

---

## 1. Requisitos Previos

Antes de iniciar el sistema, asegúrate de tener instalado y configurado lo siguiente:

* **Java Development Kit (JDK) 21**:
  * Configura la variable de entorno `JAVA_HOME`.
  * Verifica ejecutando `java -version`.
* **Apache Maven 3.9+**:
  * Configura el ejecutable de Maven en el `PATH` del sistema.
  * Verifica ejecutando `mvn -version`.
* **Servidor MySQL**:
  * Deberá estar ejecutándose localmente en el puerto `3307` (o el puerto configurado en los archivos `application.properties` de cada servicio).
  * Contraseña de `root`: Vacía (`""`) por defecto.
  * Deben estar creadas (o configurado el auto-creado en Hibernate) las bases de datos descritas en `bd-general.sql`.

---

## 2. Configuración de Base de Datos

Ejecuta el script consolidado [bd-general.sql](../bd-general.sql) en tu cliente MySQL favorito (MySQL Workbench, DBeaver, etc.) para crear todas las bases de datos y la estructura inicial de tablas necesarias para el funcionamiento del ecosistema.

Las bases de datos que se inicializan son:
* `db_auth` (Servicio de Autenticación)
* `db_security` (Servicio de Auditoría y Listas Negras)
* `db_usuarios` (Servicio de Gestión de Perfiles y Roles)
* `db_productos` (Servicio de Catálogo de Productos y Categorías)
* `db_proveedores` (Servicio de Proveedores)
* `db_stock` (Servicio de Control de Existencias)
* `db_documentos` (Servicio de Carga de Documentos)
* `db_movimientos` (Servicio de Transacciones de Bodega)
* `db_validaciones` (Servicio de Validaciones de Negocio)
* `db_reportes` (Servicio de Auditoría/Reportes Consolidados)

---

## 3. Compilación del Proyecto

Para compilar todo el ecosistema y generar los archivos empaquetados `.jar`, ejecuta el script automatizado desde la raíz del directorio `PokeStock`:

```bash
# Ejecutar desde consola o haciendo doble clic
1_build.bat
```

Este script ejecutará internamente:
```bash
mvn clean package -DskipTests
```
Esto creará la carpeta `target/` en cada microservicio con su respectivo ejecutable empaquetado.

---

## 4. Lanzamiento del Ecosistema de Microservicios

Para iniciar todos los servicios en el orden obligatorio y correcto, ejecuta el script automatizado:

```bash
# Ejecutar desde consola o haciendo doble clic
2_start.bat
```

### Orden Obligatorio de Inicialización:
1. **Eureka Server**: Se inicializa en el puerto `8761`. El script realiza una pausa de **15 segundos** para permitir que el servidor levante por completo antes de continuar.
2. **Servicios de Negocio y Seguridad**: Se inicializan todos en paralelo y en segundo plano:
   * `ms-auth` (Puerto 8081)
   * `ms-security` (Puerto 8082)
   * `ms-usuarios` (Puerto 8083)
   * `ms-proveedores` (Puerto 8084)
   * `ms-movimientos` (Puerto 8085)
   * `ms-productos` (Puerto 8086)
   * `ms-documentos` (Puerto 8087)
   * `ms-stock` (Puerto 8088)
   * `ms-validaciones` (Puerto 8089)
   * `ms-reportes` (Puerto 8090)
   * El script realiza una pausa de **20 segundos** para permitir el registro correcto de todos los servicios en Eureka.
3. **API Gateway**: Se inicializa en el puerto `8080` al final, estableciendo el enrutamiento general.

---

## 5. Verificación de Funcionamiento

* **Dashboard de Eureka**: Accede a [http://localhost:8761](http://localhost:8761) en tu navegador para verificar que los 11 microservicios clientes aparezcan registrados con estado `UP`.
* **API Gateway / Acceso**: Las peticiones del cliente externo o frontend deben apuntar únicamente al puerto `8080` (ej: [http://localhost:8080/api/productos](http://localhost:8080/api/productos)).
