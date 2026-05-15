# 🃏 PokeStock

<p align="center">
  <img src="./pokestock-banner.png" width="700" alt="PokeStock Banner">
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
| `ms-auth` | Por configurar | Autenticación |
| `ms-security` | Por configurar | Seguridad |
| `ms-usuarios` | Por configurar | Gestión de usuarios |

> Los servicios `ms-auth`, `ms-security` y `ms-usuarios` se encuentran dentro de `PokeStock/Seguridad`.

---

## 🧰 Tecnologías utilizadas

```txt
Java 21
Spring Boot
Spring Web
Spring Data JPA
Spring Cloud
Eureka Server
API Gateway
OpenFeign
Spring Security
XAMPP con MySQL puerto 3307
Lombok
Validation
Maven
Postman
GitHub
Visual Studio Code/Windsurf
```

---

## 🏗️ Arquitectura del sistema

PokeStock utiliza una arquitectura basada en **microservicios desacoplados**.

Cada microservicio funciona como una aplicación Spring Boot independiente, con su propio `pom.xml`, configuración, puerto y responsabilidad específica.

La arquitectura considera:

- **Eureka Server** para registrar y descubrir microservicios.
- **API Gateway** como punto de entrada único.
- **Microservicios de negocio** separados por responsabilidad.
- **Comunicación entre servicios** mediante REST/OpenFeign.
- **Persistencia real** con MySQL, JPA e Hibernate.
- **Validaciones** mediante Bean Validation.
- **Organización por capas** aplicando el patrón CSR.

---

## 🔄 Flujo general de comunicación

```txt
Cliente / Postman
       │
       ▼
API Gateway
       │
       ▼
Eureka Server
       │
       ▼
┌───────────────────┬───────────────────┬───────────────────┐
│   ms-productos    │  ms-proveedores   │     ms-stock      │
└───────────────────┴───────────────────┴───────────────────┘
       │                   │                   │
       └──────────────┬────┴────┬──────────────┘
                      ▼         ▼
              ms-validaciones  ms-documentos
                      │         │
                      ▼         ▼
                     ms-movimientos
                           │
                           ▼
                      ms-reportes
```
---

## 🧱 Patrón CSR

Cada microservicio está organizado bajo el patrón **CSR**:

```txt
Controller → Service → Repository
```

| Capa | Responsabilidad |
|------|----------------|
| Controller | Recibe solicitudes HTTP y expone endpoints REST |
| Service | Contiene la lógica de negocio y validaciones |
| Repository | Accede a la base de datos mediante JPA |
| Model/Entity | Representa las entidades persistentes |
| DTO | Transporta datos entre capas o servicios |

---

## ⚙️ Funcionalidades implementadas

El sistema PokeStock implementa funcionalidades distribuidas en microservicios independientes.

### 📦 Productos

- Registro de productos Pokémon TCG.
- Consulta de productos.
- Actualización de información.
- Eliminación o desactivación de productos.
- Persistencia de datos en MySQL.

### 🚚 Proveedores

- Registro de proveedores.
- Consulta de proveedores.
- Actualización de información de contacto.
- Desactivación de proveedores.
- Validación de proveedores activos.

### 📊 Stock

- Control de cantidades disponibles.
- Consulta de inventario.
- Actualización de stock.
- Validación de disponibilidad antes de movimientos.

### 🔄 Movimientos

- Registro de entradas y salidas de inventario.
- Coordinación entre productos, stock, proveedores, validaciones y documentos.
- Orquestación de operaciones principales del sistema.

### 📄 Documentos

- Registro de documentos asociados a movimientos.
- Consulta de documentos.
- Apoyo a la trazabilidad de operaciones.

### ✅ Validaciones

- Validación de reglas de negocio.
- Verificación de datos antes de ejecutar operaciones.
- Validación de stock, productos y proveedores.

### 📈 Reportes

- Consulta de reportes de inventario.
- Auditoría de operaciones.
- Seguimiento de movimientos registrados.

### 🔐 Seguridad y usuarios

- Gestión base de usuarios.
- Separación de servicios de autenticación y seguridad.
- Preparación para protección de endpoints.

---

## 📌 Reglas de negocio principales

El sistema considera reglas de negocio relacionadas con el inventario y distribución de productos Pokémon TCG:

- No registrar salidas si no existe stock suficiente.
- Validar la existencia de productos antes de operar con ellos.
- Validar proveedores antes de asociarlos a operaciones.
- Actualizar stock al registrar entradas o salidas.
- Mantener trazabilidad de movimientos mediante documentos.
- Evitar operaciones con datos incompletos o inválidos.
- Retornar respuestas claras ante errores de validación.

---

## 🗄️ Base de datos

El proyecto utiliza MySQL mediante XAMPP en el puerto:

```txt
3307
```

La carpeta `BD` contiene archivos relacionados con la creación de bases de datos y scripts iniciales:

```txt
BD/Crear BD.txt
BD/db_documentos.txt
BD/db_proveedores.txt
```

Cada microservicio posee su propia configuración de persistencia en:

```txt
src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_productos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
```

---

## ▶️ Pasos para ejecutar el proyecto

### 1. Requisitos previos

Antes de ejecutar el sistema se necesita tener instalado:

```txt
Java JDK 21
Apache Maven
XAMPP
MySQL
Postman
Git o GitHub Desktop
Editor de codigo
```

---

### 2. Clonar o descargar el repositorio

```bash
git clone https://github.com/FernandoZapataDUOC/PokeStock-FullStack1.git
cd PokeStock-FullStack1
```

Si se usa GitHub Desktop, basta con clonar el repositorio y abrir la carpeta local en su Editor de codigo.

---

### 3. Iniciar base de datos

Abrir XAMPP e iniciar:

```txt
Apache
MySQL
```

Verificar que MySQL esté funcionando en el puerto:

```txt
3307
```

Luego crear las bases de datos necesarias o ejecutar los scripts incluidos en la carpeta `BD`.

---

### 4. Verificar configuración

Antes de levantar los servicios, revisar en cada microservicio el archivo:

```txt
src/main/resources/application.properties
```

Verificar:

```properties
server.port=PUERTO_DEL_MICROSERVICIO
spring.datasource.url=jdbc:mysql://localhost:3307/NOMBRE_BD
spring.datasource.username=root
spring.datasource.password=
```

---

## 🚀 Orden recomendado de ejecución

Para ejecutar cualquier microservicio se usa el siguiente formato general:

```bash
cd PokeStock/NOMBRE_DEL_MICROSERVICIO
mvn spring-boot:run
```

Ejemplo:

```bash
cd PokeStock/ms-productos
mvn spring-boot:run
```

Para servicios dentro de la carpeta `Seguridad`, el formato es:

```bash
cd PokeStock/Seguridad/NOMBRE_DEL_MICROSERVICIO
mvn spring-boot:run
```

Ejemplo:

```bash
cd PokeStock/Seguridad/ms-usuarios
mvn spring-boot:run
```

---

### Orden de inicio

Se recomienda levantar los servicios en este orden:

```txt
1. eureka-server
2. api-gateway
3. ms-productos
4. ms-proveedores
5. ms-stock
6. ms-documentos
7. ms-validaciones
8. ms-movimientos
9. ms-reportes
10. ms-auth / ms-security / ms-usuarios
```

Eureka Server queda disponible en:

```txt
http://localhost:8761
```

API Gateway queda disponible en:

```txt
http://localhost:8080
```

---

## 🧪 Pruebas con Postman

El sistema fue pensado para ser probado mediante Postman.

Se recomienda probar:

- Endpoints CRUD de productos.
- Endpoints CRUD de proveedores.
- Consulta y actualización de stock.
- Registro de movimientos.
- Validaciones de reglas de negocio.
- Generación o consulta de documentos.
- Consulta de reportes.
- Comunicación entre microservicios mediante API Gateway.

Ejemplos de URLs base:

```txt
http://localhost:8080
http://localhost:8084
http://localhost:8085
http://localhost:8086
http://localhost:8087
http://localhost:8088
http://localhost:8089
http://localhost:8090
```

Ejemplo de prueba directa:

```txt
GET http://localhost:8086/api/productos
```

Ejemplo mediante Gateway:

```txt
GET http://localhost:8080/api/productos
```

> Las rutas exactas pueden variar según la configuración de controladores y del API Gateway.

---
