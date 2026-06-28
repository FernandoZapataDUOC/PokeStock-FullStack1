# Guía de Ejecución de Pruebas Unitarias y Cobertura (JaCoCo)

Esta guía detalla el entorno de pruebas de PokeStock, explicando cómo ejecutar los tests unitarios construidos con **JUnit 5** y **Mockito**, y cómo generar y analizar los reportes de cobertura de código mediante **JaCoCo** (Java Code Coverage).

---

## 1. Arquitectura de Pruebas en PokeStock

El proyecto utiliza una estrategia de pruebas unitarias piramidal enfocada en aislar las dependencias externas (bases de datos y otros microservicios conectados por Feign):

* **JUnit 5 (Jupiter)**: Motor principal para estructurar y correr las aserciones de las pruebas.
* **Mockito**: Utilizado para simular el comportamiento de repositorios JPA (`Spring Data Repositories`) y clientes REST de OpenFeign, asegurando que los tests de la capa de servicio sean puramente unitarios y rápidos de ejecutar.
* **JaCoCo**: Agente que analiza dinámicamente las líneas de código ejecutadas por las pruebas y genera métricas de cobertura.

---

## 2. Configuración en Maven (POM Padre)

La gestión del plugin de JaCoCo se declara centralizadamente en el `pom.xml` padre, permitiendo que todos los microservicios ejecuten la métrica bajo las mismas condiciones.

El ciclo de vida se integra de la siguiente forma:
* **Fase `initialize`**: Inicia el agente de JaCoCo que recolecta los datos de ejecución.
* **Fase `verify`**: Genera los reportes tras la ejecución de las pruebas.

---

## 3. Instrucciones de Ejecución de Pruebas

Para ejecutar las pruebas y recolectar las métricas de cobertura, abre una terminal en la carpeta raíz del proyecto (`PokeStock`) y ejecuta el comando estándar de Maven:

```bash
# Limpia, corre todos los tests y genera los reportes de JaCoCo
mvn clean verify
```

> [!IMPORTANT]
> No uses `-DskipTests` ni `-Dmaven.test.skip=true`, de lo contrario, las pruebas se saltarán y JaCoCo no generará datos de cobertura.

---

## 4. Visualización de Reportes de Cobertura

Una vez que el comando `mvn clean verify` finaliza exitosamente con un estado `BUILD SUCCESS`, JaCoCo compilará un reporte interactivo en formato HTML para cada microservicio.

Puedes abrir e inspeccionar los reportes desde tu navegador web accediendo a la siguiente ruta dentro del target de cada servicio:

```text
[NOMBRE_DEL_MICROSERVICIO]/target/site/jacoco/index.html
```

### Rutas locales de reportes listos para ver:
* [Reporte ms-productos](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-productos/target/site/jacoco/index.html)
* [Reporte ms-stock](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-stock/target/site/jacoco/index.html)
* [Reporte ms-movimientos](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-movimientos/target/site/jacoco/index.html)
* [Reporte ms-documentos](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-documentos/target/site/jacoco/index.html)
* [Reporte ms-proveedores](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-proveedores/target/site/jacoco/index.html)
* [Reporte ms-validaciones](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-validaciones/target/site/jacoco/index.html)
* [Reporte ms-reportes](file:///C:/Users/Fernando/Documents/code/PokeStock-FullStack1/PokeStock/ms-reportes/target/site/jacoco/index.html)

---

## 5. Análisis del Reporte JaCoCo

Al abrir el archivo `index.html` en un navegador, se presentará una tabla interactiva con las siguientes métricas clave:

1. **Element (Elemento)**: Muestra la estructura de paquetes, clases y métodos.
2. **Missed Instructions (Instrucciones no cubiertas)**: Cantidad de byte-code de Java que no fue ejecutado por las pruebas.
3. **Missed Branches (Ramas no cubiertas)**: Cobertura de decisiones de control (`if/else` y `switch`). Si una condición no fue evaluada tanto para verdadero como para falso, se marcará como cobertura incompleta.
4. **Covy (Porcentaje de cobertura)**: Representación visual (barra verde/roja) del porcentaje final de cobertura alcanzado.

### Indicadores de color en el código fuente:
Al hacer clic en una clase dentro del reporte de JaCoCo, verás el código fuente coloreado:
* **Verde**: Línea de código completamente ejercitada por las pruebas unitarias.
* **Amarillo**: Rama de control ejecutada de manera parcial (ej. se evaluó el `if` pero nunca el camino alternativo).
* **Rojo**: Línea de código completamente ignorada por la suite de pruebas.
