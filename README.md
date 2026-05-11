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

## 🧩 Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `eureka-server` | `8761` | Registro y descubrimiento de microservicios |
| `api-gateway` | `8080` | Punto de entrada único del sistema |
| `ms-productos` | `8086` | Catálogo de productos Pokémon TCG |
| `ms-proveedores` | `8084` | Gestión de proveedores |
| `ms-stock` | `8088` | Control de inventario |
| `ms-documentos` | `8087` | Documentación de movimientos |
| `ms-validaciones` | `8089` | Validación de reglas de negocio |
| `ms-movimientos` | `8085` | Orquestador principal |
| `ms-reportes` | `8090` | Reportes y auditoría |

---

## 🧰 Tecnologías utilizadas

```txt
Java 21
Spring Boot
Spring Web
Spring Data JPA
XAMPP con MySQL puerto 3307
Lombok
Validation
Maven
Postman
GitHub
