-- =====================================================================
-- POKESTOCK GENERAL DATABASE SCHEMA
-- CONSOLIDATED SCHEMAS FOR ALL MICROSERVICES
-- =====================================================================

-- 1. DATABASE: db_auth (ms-auth)
CREATE DATABASE IF NOT EXISTS db_auth;
USE db_auth;

CREATE TABLE IF NOT EXISTS usuarios_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE
);

-- 2. DATABASE: db_security (ms-security)
CREATE DATABASE IF NOT EXISTS db_security;
USE db_security;

CREATE TABLE IF NOT EXISTS auditoria_seguridad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    evento VARCHAR(100) NOT NULL,
    detalles TEXT,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    bloqueado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. DATABASE: db_usuarios (ms-usuarios)
CREATE DATABASE IF NOT EXISTS db_usuarios;
USE db_usuarios;

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. DATABASE: db_productos (ms-productos)
CREATE DATABASE IF NOT EXISTS db_productos;
USE db_productos;

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    categoria_id BIGINT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
);

-- 5. DATABASE: db_proveedores (ms-proveedores)
CREATE DATABASE IF NOT EXISTS db_proveedores;
USE db_proveedores;

CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(20) NOT NULL UNIQUE,
    nombre_empresa VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE
);

-- 6. DATABASE: db_stock (ms-stock)
CREATE DATABASE IF NOT EXISTS db_stock;
USE db_stock;

CREATE TABLE IF NOT EXISTS stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    lote VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    ubicacion VARCHAR(100) NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. DATABASE: db_documentos (ms-documentos)
CREATE DATABASE IF NOT EXISTS db_documentos;
USE db_documentos;

CREATE TABLE IF NOT EXISTS documentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(50) NOT NULL, -- FACTURA, GUIA_DESPACHO, BOLETA, etc.
    numero_documento VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision DATE NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    url_archivo VARCHAR(255),
    estado VARCHAR(30) DEFAULT 'PENDIENTE'
);

-- 8. DATABASE: db_movimientos (ms-movimientos)
CREATE DATABASE IF NOT EXISTS db_movimientos;
USE db_movimientos;

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento VARCHAR(20) NOT NULL, -- ENTRADA, SALIDA
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    documento_id BIGINT,
    proveedor_id BIGINT,
    usuario_id BIGINT,
    estado VARCHAR(30) DEFAULT 'COMPLETADO'
);

-- 9. DATABASE: db_validaciones (ms-validaciones)
CREATE DATABASE IF NOT EXISTS db_validaciones;
USE db_validaciones;

CREATE TABLE IF NOT EXISTS validaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(50) NOT NULL,
    numero_documento VARCHAR(50) NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    estado_validacion VARCHAR(30) NOT NULL, -- APROBADA, RECHAZADA, PENDIENTE
    observaciones TEXT,
    fecha_validacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. DATABASE: db_reportes (ms-reportes)
CREATE DATABASE IF NOT EXISTS db_reportes;
USE db_reportes;

CREATE TABLE IF NOT EXISTS reportes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255),
    resultado_json TEXT
);
