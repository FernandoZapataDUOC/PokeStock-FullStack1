# Catálogo de Endpoints de PokeStock

Todos los microservicios están accesibles externamente a través del API Gateway en el puerto `8080`.

---

## 1. Módulo de Seguridad y Autenticación

### ms-auth (`/api/auth/**`)
* `POST /api/auth/register` - Registro de nuevos usuarios.
* `POST /api/auth/login` - Inicio de sesión. Retorna el token JWT.

### ms-security (`/api/security/**`)
* `POST /api/security/audits` - Registrar evento de auditoría interna.
* `GET /api/security/audits` - Obtener todos los registros de auditorías.
* `GET /api/security/audits/usuario/{username}` - Obtener auditorías por usuario.
* `POST /api/security/tokens/blacklist` - Registrar token en la lista negra (bloqueo/logout).
* `GET /api/security/tokens/check-blacklist` - Verificar si un token está bloqueado.
* `DELETE /api/security/tokens/cleanup` - Limpiar tokens expirados de la base de datos.

### ms-usuarios (`/api/usuarios/**`, `/api/roles/**`)
* `POST /api/usuarios` - Crear un usuario.
* `GET /api/usuarios` - Listar todos los usuarios.
* `GET /api/usuarios/{id}` - Obtener usuario por ID.
* `PUT /api/usuarios/{id}` - Actualizar datos de un usuario.
* `DELETE /api/usuarios/{id}` - Eliminar (desactivar) un usuario.
* `POST /api/roles` - Crear un rol.
* `GET /api/roles` - Listar roles disponibles.

---

## 2. Módulo de Negocio e Inventario

### ms-productos (`/api/productos/**`, `/api/categorias/**`)
* `POST /api/productos` - Registrar nuevo producto.
* `GET /api/productos` - Listar productos activos.
* `GET /api/productos/{id}` - Obtener producto por ID.
* `PUT /api/productos/{id}` - Actualizar detalles de producto.
* `DELETE /api/productos/{id}` - Dar de baja un producto.
* `POST /api/categorias` - Crear categoría.
* `GET /api/categorias` - Listar categorías.

### ms-proveedores (`/api/proveedores/**`)
* `POST /api/proveedores` - Registrar proveedor.
* `GET /api/proveedores` - Listar proveedores.
* `GET /api/proveedores/{id}` - Obtener proveedor por ID.
* `PUT /api/proveedores/{id}` - Actualizar datos de proveedor.
* `DELETE /api/proveedores/{id}` - Dar de baja un proveedor.

### ms-stock (`/api/stock/**`)
* `POST /api/stock` - Agregar o inicializar stock para un producto.
* `GET /api/stock/producto/{id}` - Consultar stock disponible de un producto.
* `PUT /api/stock/actualizar` - Modificar cantidades de stock físicas.

### ms-documentos (`/api/documentos/**`)
* `POST /api/documentos` - Cargar y registrar nuevo documento de respaldo.
* `GET /api/documentos` - Listar documentos cargados.
* `GET /api/documentos/{id}` - Obtener documento por ID.

### ms-movimientos (`/api/movimientos/**`)
* `POST /api/movimientos` - Registrar un movimiento de entrada o salida de inventario.
* `GET /api/movimientos` - Obtener historial de movimientos.
* `GET /api/movimientos/producto/{id}` - Obtener movimientos asociados a un producto.

### ms-validaciones (`/api/validaciones/**`)
* `POST /api/validaciones` - Procesar y validar un documento de compra/guía.
* `GET /api/validaciones` - Obtener historial de validaciones.

### ms-reportes (`/api/reportes/**`)
* `POST /api/reportes/inventario` - Generar reporte consolidado de inventario.
* `GET /api/reportes` - Consultar reportes históricos generados.
