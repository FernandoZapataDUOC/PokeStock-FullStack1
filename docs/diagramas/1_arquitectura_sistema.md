# Diagrama de Arquitectura del Sistema (PokeStock)

Este diagrama representa la arquitectura física y lógica del ecosistema de microservicios de PokeStock, ilustrando el flujo de tráfico, descubrimiento de servicios y persistencia distribuida.

```mermaid
graph TD
    %% Cliente Externo
    Cliente([Cliente / Postman]) -->|HTTP Port 8080| Gateway[API Gateway :8080]

    %% Descubrimiento e Infraestructura
    Gateway -->|Consulta Rutas| Eureka[Eureka Server :8761]
    
    subgraph Ecosistema de Microservicios PokeStock
        %% API Gateway Redirección
        Gateway -.->|lb://ms-auth| Auth[ms-auth :8081]
        Gateway -.->|lb://ms-security| Security[ms-security :8082]
        Gateway -.->|lb://ms-usuarios| Usuarios[ms-usuarios :8083]
        Gateway -.->|lb://ms-productos| Productos[ms-productos :8086]
        Gateway -.->|lb://ms-proveedores| Proveedores[ms-proveedores :8084]
        Gateway -.->|lb://ms-stock| Stock[ms-stock :8088]
        Gateway -.->|lb://ms-documentos| Documentos[ms-documentos :8087]
        Gateway -.->|lb://ms-movimientos| Movimientos[ms-movimientos :8085]
        Gateway -.->|lb://ms-validaciones| Validaciones[ms-validaciones :8089]
        Gateway -.->|lb://ms-reportes| Reportes[ms-reportes :8090]

        %% Registro en Eureka
        Auth & Security & Usuarios & Productos & Proveedores & Stock & Documentos & Movimientos & Validaciones & Reportes -.->|Registro y Heartbeat| Eureka
        
        %% Intercomunicación Feign
        Movimientos -->|Feign| Productos
        Movimientos -->|Feign| Proveedores
        Movimientos -->|Feign| Stock
        Movimientos -->|Feign| Documentos
        Reportes -->|Feign| Stock
        Reportes -->|Feign| Productos
        Auth -->|Feign| Usuarios
        Auth -->|Feign| Security
    end

    %% Persistencia Independiente
    subgraph Servidor MySQL :3307
        Auth --->|JPA| db_auth[(db_auth)]
        Security --->|JPA| db_security[(db_security)]
        Usuarios --->|JPA| db_usuarios[(db_usuarios)]
        Productos --->|JPA| db_productos[(db_productos)]
        Proveedores --->|JPA| db_proveedores[(db_proveedores)]
        Stock --->|JPA| db_stock[(db_stock)]
        Documentos --->|JPA| db_documentos[(db_documentos)]
        Movimientos --->|JPA| db_movimientos[(db_movimientos)]
        Validaciones --->|JPA| db_validaciones[(db_validaciones)]
        Reportes --->|JPA| db_reportes[(db_reportes)]
    end

    %% Estilos de Nodos
    style Cliente fill:#f9f,stroke:#333,stroke-width:2px
    style Gateway fill:#bbf,stroke:#333,stroke-width:2px
    style Eureka fill:#ffb,stroke:#333,stroke-width:2px
