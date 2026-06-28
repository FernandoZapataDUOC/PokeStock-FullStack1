# 🚀 POKESTOCK SISTEMA DE MICROSERVICIOS MULTIMÓDULO - ENTREGA FINAL

## 📦 COMPONENTES DE DISTRIBUCIÓN Y DEFENSA TÉCNICA

Utilice los siguientes enlaces externos para descargar las versiones listas para producción y visualizar la defensa del proyecto:

| Componente | Descripción | Enlace de Descarga (Nube externa) |
| :--- | :--- | :--- |
| **📦 Versión Sin Docker** <br>*(Arranque Nativo)* | Archivo `.zip` que contiene la carpeta `apps/` con los `.jar` compilados y el script `arrancar-nativo.bat` ordenado por fases. | [Descargar ZIP Nativo aquí](https://drive.google.com/file/d/1CyITzAVRj-GVMABoAHHSql6WHKxjoiVp/view?usp=sharing) |
| **🐳 Versión Con Docker** <br>*(Avance Examen Transversal)* | Archivo `.zip` que contiene la carpeta `apps/` con los `.jar`, el archivo `docker-compose.yml` y el script automatizado `arrancar-sistema.bat`. | [Descargar ZIP Docker aquí](https://drive.google.com/file/d/1Q5KF25BAgjDaj4qMnMGr4qruDykMVr0i/view?usp=sharing) |
| **🎥 Video de Defensa Técnica** <br>*(Evaluación Individual)* | Enlace directo al video explicativo donde se evidencia el funcionamiento, testing y el aporte técnico individual. **Duración ideal: 15 minutos (Máximo permitido: 18 minutos).** Incluye subtítulos integrados o el archivo complementario `subtitulos-video.txt` en la entrega. | [Ver Video Explicativo aquí](ENLACE_A_VIDEO_AQUÍ) |

---

# 🃏 PokeStock - Sistema de Microservicios

PokeStock es un sistema completo para la gestión de inventario, proveedores y distribución de productos Pokémon TCG (Trading Card Game). Permite administrar productos, stock físico, movimientos de bodega, documentos tributarios de respaldo, validaciones comerciales y reportes, todo protegido mediante seguridad basada en JSON Web Tokens (JWT) y auditoría.

---

> [!TIP]
> **💡 DOCUMENTACIÓN TÉCNICA DETALLADA**
> Para un entendimiento profundo del modelado de datos, diagramas y flujos de seguridad de tokens JWT, recomendamos explorar la carpeta **[docs/](docs/)** en la raíz de este repositorio, la cual contiene:
> * **[docs/bd-general.sql](docs/bd-general.sql)**: Script de base de datos consolidado para todo el ecosistema.
> * **[docs/endpoints.md](docs/endpoints.md)**: Catálogo estructurado de endpoints expuestos en el API Gateway.
> * **[docs/diagramas/](docs/diagramas/)**: Diagramas de Arquitectura del Sistema y de Secuencia de Flujos Críticos.
> * **[docs/documentación/](docs/documentación/)**: Guías de Despliegue, Seguridad/Roles, Ejecución de Pruebas Unitarias y Cobertura con JaCoCo junto a la colección Postman lista para importar.

---

## 👥 Colaboradores

| Integrante | GitHub |
|-----------|--------|
| Fernando Zapata | [@DopeZeta](https://github.com/FernandoZapataDUOC) |
| Benjamin Gomez | [@BenGomezDUOC](https://github.com/BenGomezDUOC) |

---

# 1. Objetivo del proyecto

El sistema administra todo el flujo operacional de una distribuidora de cartas coleccionables Pokémon:

1. Autenticar accesos e inicio de sesión de personal con perfiles y permisos delimitados.
2. Registrar categorías de cartas y productos.
3. Administrar proveedores oficiales.
4. Inicializar y controlar stock físico en bodegas principales.
5. Gestionar movimientos de inventario (Entrada/Salida) requiriendo validación documental y comercial antes de su despacho.
6. Auditar accesos e invalidar tokens JWT al cerrar sesión.
7. Generar reportes históricos detallados y consolidados.

---

# 2. Arquitectura general

```text
Cliente externo / Postman / Navegador
        |
        v
API Gateway :8080 (Mapeo general de rutas)
        |
        +--> ms-auth       :8081  -> db_auth
        +--> ms-security   :8082  -> db_security
        +--> ms-usuarios   :8083  -> db_usuarios
        +--> ms-proveedores:8084  -> db_proveedores
        +--> ms-movimientos:8085  -> db_movimientos
        +--> ms-productos  :8086  -> db_productos
        +--> ms-documentos :8087  -> db_documentos
        +--> ms-stock      :8088  -> db_stock
        +--> ms-validaciones:8089 -> db_validaciones
        +--> ms-reportes   :8090  -> db_reportes

Eureka Server :8761
```

---

# 3. Microservicios del sistema

| Módulo | Puerto | Responsabilidad |
| :--- | :---: | :--- |
| `eureka-server` | 8761 | Registro y descubrimiento dinámico de microservicios. |
| `api-gateway` | 8080 | Entrada única, enrutamiento RBAC y validación perimetral de JWT. |
| `ms-auth` | 8081 | Emisión de tokens de acceso JWT y registro seguro. |
| `ms-security` | 8082 | Auditoría y lista negra (blacklist) de tokens revocados. |
| `ms-usuarios` | 8083 | Gestión de credenciales de usuarios y roles del sistema. |
| `ms-proveedores` | 8084 | Mantenedor y validación de datos de proveedores de distribución. |
| `ms-movimientos` | 8085 | Orquestador de transacciones de inventario (Entrada/Salida). |
| `ms-productos` | 8086 | Catálogo general de productos y categorías de Pokémon TCG. |
| `ms-documentos` | 8087 | Carga y gestión documental de respaldo (Facturas, Guías). |
| `ms-stock` | 8088 | Control y actualización de cantidades de existencias físicas por lote. |
| `ms-validaciones` | 8089 | Auditoría y validación comercial interna de operaciones. |
| `ms-reportes` | 8090 | Generación de reportes de inventario consolidados guardados en JSON. |

---

# 4. Tecnologías utilizadas

* **Lenguaje:** Java 21
* **Framework Base:** Spring Boot 3.5.15
* **Herramientas Cloud:** Spring Cloud Gateway, Eureka Server/Client, OpenFeign
* **Bases de Datos & Persistencia:** MySQL (Puerto 3307), Spring Data JPA, Hibernate
* **Seguridad:** Spring Security Crypto, JJWT (v0.12.5)
* **Documentación & QA:** Swagger / OpenAPI 3, JUnit 5, Mockito, JaCoCo (Cobertura)
* **Otros:** Lombok, Bean Validation, Maven, Postman

---

# 5. Estructura del proyecto

```text
PokeStock-FullStack1/
│
├── pom.xml                                <-- POM Padre
├── README.md                              <-- Guía Principal
│
├── docs/                                  <-- Documentación
│   ├── bd-general.sql
│   ├── endpoints.md
│   ├── diagramas/
│   └── documentación/
│
├── PokeStock/                             <-- Código Fuente
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── ms-productos/
│   ├── ms-proveedores/
│   ├── ms-stock/
│   ├── ms-documentos/
│   ├── ms-movimientos/
│   ├── ms-validaciones/
│   ├── ms-reportes/
│   └── Seguridad/
│       ├── ms-auth/
│       ├── ms-security/
│       └── ms-usuarios/
```

---

# 6. Bases de datos

El proyecto usa una base de datos independiente por microservicio.

| Microservicio | Base de datos | Tabla principal |
| :--- | :--- | :--- |
| `ms-auth` | `db_auth` | `usuarios_auth` |
| `ms-security` | `db_security` | `auditoria_seguridad`, `token_blacklist` |
| `ms-usuarios` | `db_usuarios` | `usuarios`, `roles`, `usuario_roles` |
| `ms-productos` | `db_productos` | `productos`, `categorias` |
| `ms-proveedores` | `db_proveedores` | `proveedores` |
| `ms-stock` | `db_stock` | `stocks` |
| `ms-documentos` | `db_documentos` | `documentos` |
| `ms-movimientos`| `db_movimientos` | `movimientos` |
| `db_validaciones`| `db_validaciones` | `validaciones` |
| `db_reportes` | `db_reportes` | `reportes` |

El script de creación consolidado se encuentra en: `docs/bd-general.sql`.

---

# 7. Configuración de MySQL

Este proyecto está configurado para usar MySQL local en el puerto: `3307`.

Ejemplo de configuración en los microservicios:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_productos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

# 8. Orden de ejecución

Los servicios deben iniciarse en el siguiente orden estricto para asegurar la correcta comunicación:

| Orden | Servicio | Puerto |
| :---: | :--- | :---: |
| 1 | `eureka-server` | 8761 |
| 2 | `ms-auth` | 8081 |
| 3 | `ms-security` | 8082 |
| 4 | `ms-usuarios` | 8083 |
| 5 | `ms-proveedores` | 8084 |
| 6 | `ms-productos` | 8086 |
| 7 | `ms-stock` | 8088 |
| 8 | `ms-documentos` | 8087 |
| 9 | `ms-validaciones` | 8089 |
| 10 | `ms-movimientos` | 8085 |
| 11 | `ms-reportes` | 8090 |
| 12 | `api-gateway` | 8080 (Último) |

---

# 9. Ejecución desde VSCode / IDE

Puedes utilizar la herramienta **Spring Boot Dashboard** del IDE para levantar los módulos uno por uno siguiendo el orden de la tabla.

Para arrancar un servicio desde consola:
```bash
cd PokeStock/eureka-server
mvn spring-boot:run
```

Para una ejecución automatizada, descomprime la versión lista para producción (`PokeStock_Nativo.zip`) y ejecuta el script:
```bash
arrancar-nativo.bat
```

---

# 10. Compilación del proyecto completo

Para compilar todo el proyecto y verificar que no existan errores estructurales sin correr los tests, ejecuta desde la raíz:
```bash
mvn clean package -DskipTests
```
*(Utiliza el POM Padre agregador para empaquetar de forma secuencial todos los módulos).*

---

# 11. Eureka Server

La consola gráfica de Eureka Server para monitorear el estado de las instancias de los servicios se encuentra disponible en:
```text
http://localhost:8761
```

---

# 12. API Gateway

El API Gateway centraliza todas las consultas en el puerto `8080`. Es el único puerto que expone endpoints para los clientes externos.

Rutas de negocio principales expuestas:
* Categorías: `/api/categorias/**`
* Productos: `/api/productos/**`
* Proveedores: `/api/proveedores/**`
* Stock: `/api/stock/**`
* Documentos: `/api/documentos/**`
* Movimientos: `/api/movimientos/**`
* Validaciones: `/api/validaciones/**`
* Reportes: `/api/reportes/**`

---

# 13. Swagger (OpenAPI)

La documentación interactiva de Swagger UI se puede revisar directamente en el puerto asignado para cada microservicio de negocio:

* ms-productos: `http://localhost:8086/swagger-ui.html`
* ms-proveedores: `http://localhost:8084/swagger-ui.html`
* ms-stock: `http://localhost:8088/swagger-ui.html`
* ms-documentos: `http://localhost:8087/swagger-ui.html`
* ms-movimientos: `http://localhost:8085/swagger-ui.html`
* ms-validaciones: `http://localhost:8089/swagger-ui.html`
* ms-reportes: `http://localhost:8090/swagger-ui.html`

---

# 14. Comunicación entre microservicios

El proyecto utiliza OpenFeign para establecer la intercomunicación entre los diferentes microservicios de manera síncrona:

| Servicio origen | Servicio destino | Objetivo de la comunicación |
| :--- | :--- | :--- |
| `ms-movimientos` | `ms-productos` | Validar que el producto ingresado exista y esté activo. |
| `ms-movimientos` | `ms-proveedores`| Validar que el proveedor ingresado exista y esté activo. |
| `ms-movimientos` | `ms-stock` | Consolidar, descontar o aumentar stock de bodega tras completarse la transacción. |
| `ms-movimientos` | `ms-documentos` | Validar que el movimiento tenga asociada documentación tributaria. |
| `ms-auth` | `ms-usuarios` | Obtener datos de credenciales, perfiles y roles del usuario registrado. |
| `ms-auth` | `ms-security` | Registrar eventos de login/auditoría y control de blacklist. |

---

# 15. Flujo funcional principal

Ejemplo básico de registro de un producto con categoría:

## Paso 1: Crear Categoría
```http
POST http://localhost:8080/api/categorias
Content-Type: application/json
```
```json
{
  "nombre": "Booster Boxes",
  "descripcion": "Cajas de sobres selladas"
}
```

## Paso 2: Crear Producto
```http
POST http://localhost:8080/api/productos
Content-Type: application/json
```
```json
{
  "nombre": "Scarlet & Violet Booster Box",
  "tipo": "BOOSTER_BOX",
  "edicion": "Scarlet & Violet",
  "idioma": "EN",
  "anioLanzamiento": 2023,
  "categoriaId": 1
}
```

---

# 16. Validaciones implementadas

## `ms-productos`
* Nombre del producto obligatorio e idioma válido.
* Relación con categorías válidas (JPA).

## `ms-proveedores`
* RUT de la empresa y correo electrónico válidos y obligatorios.
* Correo electrónico único en base de datos.

## `ms-stock`
* Cantidad física no negativa.
* Ubicación de estantería obligatoria.

---

# 17. Manejo de errores

Los microservicios interceptan excepciones a través de `@RestControllerAdvice` retornando respuestas JSON estandarizadas:

```json
{
  "fecha": "2026-06-28T16:28:40",
  "estado": 400,
  "error": "Error de validación",
  "mensaje": "Existen campos inválidos en la solicitud",
  "ruta": "/api/productos",
  "validaciones": {
    "nombre": "El nombre del producto es obligatorio"
  }
}
```

---

# 18. Logs del sistema

Se implementan logs informativos y de depuración mediante Lombok en la capa de servicios:

```java
@Slf4j
public class ProductoServiceImpl {
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        log.info("Creando producto: {}", request.getNombre());
    }
}
```

---

# 19. Comandos útiles de Maven

* **Compilar y ejecutar la suite de pruebas unitarias**: `mvn clean install`
* **Compilar y testear generando reportes JaCoCo**: `mvn clean verify`
* **Compilar excluyendo los tests**: `mvn clean package -DskipTests`
* **Compilar un microservicio específico**: `mvn clean package -pl ms-productos -DskipTests`

---

# 20. Documentación adicional

Toda la documentación detallada del proyecto se puede revisar en:
* [Docs / Endpoints](docs/endpoints.md)
* [Docs / Script SQL](docs/bd-general.sql)
* [Docs / Diagrama Arquitectura](docs/diagramas/1_arquitectura_sistema.md)
* [Docs / Guía JaCoCo](docs/documentación/guia-pruebas-cobertura-jacoco.md)

---

# 21. Estado actual del proyecto

| Elemento | Estado |
| :--- | :--- |
| Proyecto padre Maven | **Implementado** |
| Registro Eureka | **Implementado** |
| API Gateway | **Implementado** |
| Servicios de Seguridad (`ms-auth`, `ms-security`, `ms-usuarios`) | **Implementados** |
| Servicios de Negocio (Productos, Proveedores, Stock, etc.) | **Implementados** |
| Swagger OpenAPI | **Implementado** |
| Pruebas Unitarias (JUnit/Mockito) | **Implementadas** |
| Cobertura JaCoCo | **Implementado** |
| Contenerización Docker Compose | **Implementado** |

---

# 22. Próximas mejoras sugeridas

* Integrar pruebas de integración con H2 Database o Testcontainers.
* Desarrollar interfaz cliente web (Frontend reactivo o Thymeleaf).
* Configurar un servidor de Logs centralizado (ej. Grafana Loki o ELK Stack).
