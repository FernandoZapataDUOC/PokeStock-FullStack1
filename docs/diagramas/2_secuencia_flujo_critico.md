# Diagrama de Secuencia: Registro y Validación de Movimientos

Este diagrama representa el flujo crítico de registro de una entrada o salida de mercancía, la asociación de su documento de respaldo y la validación final que actualiza el stock disponible.

```mermaid
sequenceDiagram
    autonumber
    actor Cliente
    participant Gateway as API Gateway (8080)
    participant Movimientos as ms-movimientos (8085)
    participant Productos as ms-productos (8086)
    participant Proveedores as ms-proveedores (8084)
    participant Documentos as ms-documentos (8087)
    participant Stock as ms-stock (8088)

    %% Paso 1: Creación del Movimiento
    Cliente->>Gateway: POST /api/movimientos (productoId, proveedorId, cantidad)
    Gateway->>Movimientos: Redirecciona petición
    
    Note over Movimientos, Productos: Validación de Entidades vía Feign Clients
    Movimientos->>Productos: GET /api/productos/{id}
    Productos-->>Movimientos: Retorna Producto (Valido)
    
    Movimientos->>Proveedores: GET /api/proveedores/{id}
    Proveedores-->>Movimientos: Retorna Proveedor (Valido)
    
    Movimientos->>Movimientos: Crea movimiento en estado PENDIENTE
    Movimientos-->>Cliente: Retorna Movimiento Creado (con movimientoId)

    %% Paso 2: Asociación del Documento
    Cliente->>Gateway: POST /api/documentos (movimientoId, archivo.pdf)
    Gateway->>Documentos: Redirecciona petición
    Documentos->>Documentos: Guarda registro de factura/guía
    Documentos-->>Cliente: Retorna Documento Creado

    %% Paso 3: Validación y Cierre
    Cliente->>Gateway: PUT /api/movimientos/{movimientoId}/validar
    Gateway->>Movimientos: Redirecciona petición
    Movimientos->>Documentos: GET /api/documentos/movimiento/{movimientoId}
    Documentos-->>Movimientos: Retorna Documento Asociado
    Movimientos->>Movimientos: Cambia estado a VALIDADO
    Movimientos-->>Cliente: Retorna estado de validación exitoso

    %% Paso 4: Completar y actualizar stock
    Cliente->>Gateway: PUT /api/movimientos/{movimientoId}/completar
    Gateway->>Movimientos: Redirecciona petición
    Movimientos->>Stock: PUT /api/stock/actualizar (productoId, cantidad)
    Stock->>Stock: Incrementa/Decrementa unidades del lote
    Stock-->>Movimientos: Confirmación de stock actualizado
    Movimientos->>Movimientos: Cambia estado a COMPLETADO
    Movimientos-->>Cliente: Retorna Movimiento Finalizado
```
