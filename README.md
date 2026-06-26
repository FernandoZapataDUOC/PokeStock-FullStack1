# 🃏 PokeStock

<p align="center">
  <img src="docs/pokestock-banner.png" width="700" alt="PokeStock Banner">
</p>

<p align="center">
  Sistema backend para la gestión de inventario, proveedores y distribución de productos Pokémon TCG.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=springboot">
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql">
  <img src="https://img.shields.io/badge/Postman-API-orange?style=for-the-badge&logo=postman">
</p>

---

## 👥 Colaboradores

| Integrante | GitHub |
|-----------|--------|
| Fernando Zapata | [@DopeZeta](https://github.com/FernandoZapataDUOC) |
| Benjamin Gomez | [@Benjamin Gómez](https://github.com/BenGomezDUOC) |

---

## ❓ ¿Qué es PokeStock?

**PokeStock** es un sistema backend creado para administrar un negocio ficticio de distribución de cartas Pokémon.

El proyecto permite gestionar productos, controlar stock, registrar proveedores y organizar operaciones básicas de una tienda o distribuidora de Pokémon TCG.

La idea no es solo guardar datos, sino simular cómo funcionaría un sistema real para manejar productos coleccionables como cartas, sobres, cajas, accesorios y pedidos.

---

## 🎯 Objetivo del proyecto

El objetivo de **PokeStock** es construir una solución backend basada en una arquitectura de microservicios desacoplados, aplicando persistencia real, reglas de negocio, validaciones, manejo de errores, comunicación entre servicios y organización del código bajo el patrón **CSR**.

---

## 🧩 Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `eureka-server` | `8761` | Registro y descubrimiento de microservicios |
| `api-gateway` | `8080` | Punto de entrada único del sistema |
| `ms-proveedores` | `8084` | Gestión de proveedores |
| `ms-movimientos` | `8085` | Orquestador principal de movimientos de inventario |
| `ms-productos` | `8086` | Catálogo de productos Pokémon TCG |
| `ms-documentos` | `8087` | Documentación de movimientos y operaciones |
| `ms-stock` | `8088` | Control de inventario |
| `ms-validaciones` | `8089` | Validación de reglas de negocio |
| `ms-reportes` | `8090` | Reportes y auditoría |

> **Seguridad** (`ms-auth`, `ms-security`, `ms-usuarios`): módulo creado pero **no implementado en esta entrega**. Se integrará en una fase posterior del proyecto.

---

## 🧰 Tecnologías utilizadas

```txt
Java 21
Spring Boot 3.5.x
Spring Web
Spring Data JPA
Spring Cloud 2025.0.0
Eureka Server / Eureka Client
API Gateway (Spring Cloud Gateway)
OpenFeign
XAMPP con MySQL puerto 3307
Lombok
Bean Validation
Maven
Postman
GitHub
Visual Studio Code / Windsurf
```

---

## 🏗️ Arquitectura del sistema

PokeStock utiliza una arquitectura basada en **microservicios desacoplados**.

Cada microservicio funciona como una aplicación Spring Boot independiente, con su propio `pom.xml`, configuración, puerto y responsabilidad específica.

La arquitectura considera:

- **Eureka Server** para registrar y descubrir microservicios dinámicamente.
- **API Gateway** como punto de entrada único para todos los clientes.
- **Microservicios de negocio** separados por responsabilidad funcional.
- **Comunicación entre servicios** mediante OpenFeign con resolución de instancias via Eureka.
- **Persistencia real** con MySQL, JPA e Hibernate — una base de datos por microservicio.
- **Validaciones** con Bean Validation (JSR 380).
- **Organización por capas** aplicando el patrón CSR (Controller → Service → Repository).

---

## 🔄 Flujo general de comunicación

```txt
Cliente / Postman
       │
       ▼
  API Gateway (:8080)
       │
       ▼
  Eureka Server (:8761)
       │
       ▼
┌──────────────┬────────────────┬───────────┐
│ ms-productos │ ms-proveedores │  ms-stock │
└──────────────┴────────────────┴───────────┘
       │               │               │
       └───────┬────────┴───────────────┘
               ▼
  ms-validaciones   ms-documentos
               │           │
               └─────┬─────┘
                     ▼
              ms-movimientos ← orquestador principal
                     │
                     ▼
                ms-reportes
```

---

## 🧱 Patrón CSR

Cada microservicio está organizado bajo el patrón **CSR**:

| Capa | Responsabilidad |
|------|----------------|
| Controller | Recibe solicitudes HTTP y expone endpoints REST |
| Service (interfaz + impl) | Contiene la lógica de negocio y validaciones |
| Repository | Accede a la base de datos mediante JpaRepository |
| Model / Entity | Representa las entidades JPA persistentes |
| DTO (request / response) | Transporta datos entre capas sin exponer entidades |
| Client (Feign) | Consume endpoints de otros microservicios |

---

## ⚙️ Funcionalidades implementadas

### 📦 Productos (`ms-productos` — puerto 8086)
- CRUD completo de productos Pokémon TCG.
- Gestión de categorías con relación `@OneToMany` → `@ManyToOne` (JPA real).
- Un producto puede tener o no una categoría asignada.
- Soft delete: los productos se desactivan, no se eliminan físicamente.

### 🚚 Proveedores (`ms-proveedores` — puerto 8084)
- CRUD completo de proveedores.
- Validación de email único al crear y actualizar.
- Soft delete: desactivación lógica para preservar historial.

### 📊 Stock (`ms-stock` — puerto 8088)
- Control de cantidades disponibles por producto y lote.
- Endpoints para aumentar y descontar stock.
- Validación de stock negativo: no se puede descontar más de lo disponible.

### 🔄 Movimientos (`ms-movimientos` — puerto 8085)
- Orquestador principal del sistema.
- Registra entradas y salidas de inventario.
- Valida producto (ms-productos), proveedor (ms-proveedores), stock (ms-stock) y documentación (ms-documentos) via Feign.
- Manejo de errores Feign con try/catch para cada llamada remota.
- Flujo de estados: `PENDIENTE` → `VALIDADO` → `COMPLETADO` / `RECHAZADO`.
- Estado inicial forzado por `@PrePersist` — el cliente no puede establecerlo.

### 📄 Documentos (`ms-documentos` — puerto 8087)
- Registro de documentos asociados a movimientos (facturas, guías, órdenes).
- Validación de documento: no se puede validar dos veces el mismo documento.
- Un movimiento no puede validarse sin al menos un documento registrado.

### ✅ Validaciones (`ms-validaciones` — puerto 8089)
- Registro de validaciones por movimiento.
- Aprobación y rechazo manual con motivo obligatorio.
- Determina estado según contenido de la observación.

### 📈 Reportes (`ms-reportes` — puerto 8090)
- Reporte de inventario actual cruzando ms-stock con ms-productos.
- Historial de movimientos.
- Reporte por producto individual.
- Los resultados se persisten como JSON para auditoría histórica.

### 🔐 Seguridad (`ms-auth`, `ms-security`, `ms-usuarios`)
> ⚠️ **No implementado en esta entrega.** Los servicios de seguridad están creados pero pendientes de implementación para una fase posterior del proyecto.

---

## 📌 Reglas de negocio principales

- No registrar salidas si no hay stock suficiente para la cantidad solicitada.
- Validar que el producto existe y está activo antes de operar.
- Validar que el proveedor existe y está activo antes de asociarlo.
- Un movimiento no puede completarse sin haber sido validado primero.
- Un movimiento no puede validarse sin al menos un documento asociado.
- No se puede rechazar un movimiento ya completado.
- Los documentos no pueden validarse más de una vez.
- Proveedores y productos usan soft delete — no se eliminan físicamente.

---

## 🗄️ Base de datos

El proyecto usa MySQL a través de XAMPP en el puerto `3307`.

Cada microservicio tiene su propia base de datos independiente. Ejecutar el siguiente script antes de levantar los servicios:

```sql
CREATE DATABASE IF NOT EXISTS db_productos   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_proveedores CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_stock       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_documentos  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_validaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_movimientos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_reportes    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Las tablas se crean automáticamente al levantar cada servicio gracias a `spring.jpa.hibernate.ddl-auto=update`.

La carpeta `BD/` del repositorio también contiene scripts adicionales de referencia.

---

## 🚀 Pasos para ejecutar el proyecto

### 1. Prerrequisitos

```txt
Java JDK 21
Apache Maven
XAMPP (MySQL en puerto 3307)
IntelliJ IDEA o VS Code / Windsurf
Postman
Git
```

### 2. Clonar el repositorio

```bash
git clone https://github.com/FernandoZapataDUOC/PokeStock-FullStack1.git
cd PokeStock-FullStack1
```

### 3. Iniciar MySQL

Abrir XAMPP y arrancar el módulo **MySQL**. Verificar que esté corriendo en el puerto `3307`.

Luego ejecutar el script de creación de bases de datos del paso anterior.

### 4. Verificar configuración de cada servicio

En cada microservicio revisar `src/main/resources/application.properties`:

```properties
server.port=PUERTO_DEL_SERVICIO
spring.datasource.url=jdbc:mysql://localhost:3307/NOMBRE_BD?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

### 5. Orden de arranque — obligatorio

> ⚠️ El orden importa. Si se levanta el Gateway antes que Eureka, no podrá mapear los servicios.

```txt
1.  eureka-server      → http://localhost:8761
2.  ms-productos       → http://localhost:8086
3.  ms-proveedores     → http://localhost:8084
4.  ms-stock           → http://localhost:8088
5.  ms-documentos      → http://localhost:8087
6.  ms-validaciones    → http://localhost:8089
7.  ms-movimientos     → http://localhost:8085
8.  ms-reportes        → http://localhost:8090
9.  api-gateway        → http://localhost:8080  ← siempre el último
```

Para levantar cada servicio desde terminal:

```bash
cd PokeStock/NOMBRE_DEL_MICROSERVICIO
mvn spring-boot:run
```

### 6. Verificar registro en Eureka

Ir a `http://localhost:8761` y confirmar que todos los servicios aparecen con status **UP**.

---

## 🧪 Pruebas con Postman

Todas las peticiones van al **puerto 8080** (API Gateway). Eureka resuelve el servicio destino automáticamente.

### Ejemplos de endpoints principales

```http
# Productos
GET    http://localhost:8080/api/productos
GET    http://localhost:8080/api/productos/{id}
POST   http://localhost:8080/api/productos
PUT    http://localhost:8080/api/productos/{id}
DELETE http://localhost:8080/api/productos/{id}

# Proveedores
GET    http://localhost:8080/api/proveedores
POST   http://localhost:8080/api/proveedores
PUT    http://localhost:8080/api/proveedores/{id}
DELETE http://localhost:8080/api/proveedores/{id}

# Stock
GET    http://localhost:8080/api/stock
GET    http://localhost:8080/api/stock/producto/{productoId}
POST   http://localhost:8080/api/stock
PUT    http://localhost:8080/api/stock/{id}/aumentar?cantidad=10
PUT    http://localhost:8080/api/stock/{id}/descontar?cantidad=5

# Movimientos
GET    http://localhost:8080/api/movimientos
POST   http://localhost:8080/api/movimientos
PUT    http://localhost:8080/api/movimientos/{id}/validar
PUT    http://localhost:8080/api/movimientos/{id}/completar
PUT    http://localhost:8080/api/movimientos/{id}/rechazar

# Documentos
GET    http://localhost:8080/api/documentos/movimiento/{movimientoId}
POST   http://localhost:8080/api/documentos
PUT    http://localhost:8080/api/documentos/{id}/validar

# Validaciones
GET    http://localhost:8080/api/validaciones/movimiento/{movimientoId}
POST   http://localhost:8080/api/validaciones/movimiento/{movimientoId}/validar

# Reportes
GET    http://localhost:8080/api/reportes/inventario
GET    http://localhost:8080/api/reportes/movimientos
GET    http://localhost:8080/api/reportes/producto/{productoId}
```

### Flujo completo de prueba recomendado

```txt
1. POST /api/proveedores         → crear un proveedor
2. POST /api/productos           → crear un producto (con categoriaId opcional)
3. POST /api/stock               → crear stock inicial para el producto
4. POST /api/movimientos         → crear movimiento ENTRADA
5. POST /api/documentos          → registrar documento para el movimiento
6. PUT  /api/movimientos/{id}/validar   → validar el movimiento
7. PUT  /api/movimientos/{id}/completar → completar (actualiza stock)
8. GET  /api/reportes/inventario        → verificar stock actualizado
```

---

## 📁 Estructura del repositorio

```txt
PokeStock-FullStack1/
├── PokeStock/
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── ms-productos/
│   ├── ms-proveedores/
│   ├── ms-stock/
│   ├── ms-documentos/
│   ├── ms-validaciones/
│   ├── ms-movimientos/
│   ├── ms-reportes/
│   └── Seguridad/          ← no implementado en esta entrega
│       ├── ms-auth/
│       ├── ms-security/
│       └── ms-usuarios/
├── BD/                     ← scripts SQL de referencia
├── pokestock-banner.png
└── README.md
```
