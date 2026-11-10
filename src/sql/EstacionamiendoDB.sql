CREATE DATABASE IF NOT EXISTS estacionamiento_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE estacionamiento_bd;

-- 1. Tabla de Usuarios (Soporta Roles y Encriptación SHA-1)
CREATE TABLE IF NOT EXISTS usuarios (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(40) NOT NULL, -- Almacena el Hash SHA-1 (40 caracteres hexadecimales)
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'AUDITOR')),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Tabla de Configuración de Tarifas (Administrador)
CREATE TABLE IF NOT EXISTS tarifas (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       tipo_vehiculo VARCHAR(30) NOT NULL, -- 'AUTO', 'MOTOCICLETA', 'CAMIONETA'
    costo_hora DECIMAL(8,2) NOT NULL,
    costo_fraccion DECIMAL(8,2) NOT NULL,
    minutos_gracia INT DEFAULT 15,
    costo_boleto_perdido DECIMAL(8,2) DEFAULT 150.00,
    activo BOOLEAN DEFAULT TRUE
    );

-- 3. Tabla de Registro de Tickets y Accesos (Caseta)
CREATE TABLE IF NOT EXISTS tickets (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       folio VARCHAR(30) NOT NULL UNIQUE,
    placa VARCHAR(15) NOT NULL,
    tipo_vehiculo VARCHAR(30) NOT NULL,
    fecha_ingreso DATETIME NOT NULL,
    fecha_salida DATETIME NULL,
    cajon_asignado INT NOT NULL,
    monto_total DECIMAL(8,2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'EN_ESTACIONAMIENTO' CHECK (estado IN ('EN_ESTACIONAMIENTO', 'PAGADO', 'CANCELADO')),
    usuario_ingreso_id BIGINT,
    usuario_salida_id BIGINT,
    FOREIGN KEY (usuario_ingreso_id) REFERENCES usuarios(id),
    FOREIGN KEY (usuario_salida_id) REFERENCES usuarios(id)
    );

-- INSERTAR DATOS INICIALES DE PRUEBA
-- Las contraseñas se insertan encriptadas con SHA1('clave123')
-- Hash de 'admin123' -> 7c4a8d09ca3762af61e59520943dc26494f8941b
-- Hash de 'caseta123' -> 40bd001563085fc35165329ea1ff5c5ecbdbbeef

INSERT INTO usuarios (nombre, username, password, rol) VALUES
                                                           ('Vicente Soto (Admin)', 'admin', SHA1('admin123'), 'ADMINISTRADOR'),
                                                           ('Operador Caseta 1', 'operador', SHA1('caseta123'), 'OPERADOR'),
                                                           ('Auditor General', 'auditor', SHA1('auditor123'), 'AUDITOR');

-- Tarifas por defecto
INSERT INTO tarifas (tipo_vehiculo, costo_hora, costo_fraccion, minutos_gracia, costo_boleto_perdido) VALUES
                                                                                                          ('Automóvil', 20.00, 5.00, 15, 150.00),
                                                                                                          ('Motocicleta', 10.00, 3.00, 15, 100.00),
                                                                                                          ('Camioneta', 25.00, 7.00, 15, 200.00);