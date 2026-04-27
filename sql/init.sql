-- ============================================
-- Script de inicialización de base de datos
-- Se ejecuta automáticamente al crear el
-- contenedor de PostgreSQL por primera vez
-- ============================================

-- ============================================
-- Crear base de datos si no existe
-- ============================================
-- (Docker ya crea la BD con POSTGRES_DB del .env)

-- ============================================
-- Tabla: productos
-- ============================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- Tabla: inventario
-- ============================================
CREATE TABLE IF NOT EXISTS inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT uk_producto_id UNIQUE (producto_id),
    CONSTRAINT chk_cantidad_no_negativa CHECK (cantidad >= 0)
);

-- ============================================
-- Tabla: historial_compras
-- ============================================
CREATE TABLE IF NOT EXISTS historial_compras (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    descripcion VARCHAR(255),
    fecha TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_historial_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- ============================================
-- Datos dummy: productos
-- ============================================
INSERT INTO productos (nombre, precio, descripcion, fecha_creacion, fecha_modificacion)
SELECT * FROM (VALUES
    ('Laptop HP Pavilion 15',          2499999.99, 'Laptop HP Pavilion 15, procesador Intel Core i7, 16GB RAM, 512GB SSD, pantalla 15.6 pulgadas Full HD',           NOW(), NOW()),
    ('Mouse Logitech MX Master 3S',     349900.00, 'Mouse inalámbrico ergonómico con sensor de 8000 DPI, conexión Bluetooth y USB-C, batería recargable',            NOW(), NOW()),
    ('Teclado Mecánico Redragon K552',   189900.00, 'Teclado mecánico compacto TKL con switches rojos, retroiluminación LED roja, estructura de aluminio',           NOW(), NOW()),
    ('Monitor Samsung 27" 4K',         1299000.00, 'Monitor IPS 27 pulgadas resolución 4K UHD, 60Hz, HDR10, puertos HDMI y DisplayPort',                             NOW(), NOW()),
    ('Audífonos Sony WH-1000XM5',       899900.00, 'Audífonos over-ear con cancelación de ruido activa, Bluetooth 5.2, batería de 30 horas',                         NOW(), NOW()),
    ('Disco SSD Samsung 970 EVO 1TB',    459000.00, 'Disco sólido NVMe M.2, velocidad lectura 3500 MB/s, escritura 3300 MB/s',                                       NOW(), NOW()),
    ('Webcam Logitech C920 HD Pro',      279900.00, 'Cámara web Full HD 1080p a 30fps, enfoque automático, micrófono estéreo dual',                                   NOW(), NOW()),
    ('Silla Ergonómica Cougar Armor',   1150000.00, 'Silla gamer con soporte lumbar ajustable, reposabrazos 4D, reclinable hasta 180 grados',                         NOW(), NOW()),
    ('Hub USB-C Anker 7 en 1',          189900.00, 'Adaptador multipuerto con HDMI 4K, 3 puertos USB 3.0, lector SD/microSD, carga PD de 100W',                      NOW(), NOW()),
    ('Tablet Samsung Galaxy Tab S9',   2099000.00, 'Tablet 11 pulgadas, pantalla AMOLED 120Hz, procesador Snapdragon 8 Gen 2, 128GB',                                NOW(), NOW()),
    ('Cargador Inalámbrico Belkin 15W',  149900.00, 'Cargador inalámbrico Qi2 de 15W, compatible con iPhone y Android, diseño compacto con indicador LED',            NOW(), NOW())
) AS datos(nombre, precio, descripcion, fecha_creacion, fecha_modificacion)
WHERE NOT EXISTS (SELECT 1 FROM productos);

-- ============================================
-- Datos dummy: inventario
-- ============================================
INSERT INTO inventario (producto_id, cantidad, fecha_creacion, fecha_modificacion)
SELECT * FROM (VALUES
    (1::BIGINT,  50,  NOW(), NOW()),
    (2::BIGINT,  120, NOW(), NOW()),
    (3::BIGINT,  85,  NOW(), NOW()),
    (4::BIGINT,  30,  NOW(), NOW()),
    (5::BIGINT,  200, NOW(), NOW()),
    (6::BIGINT,  15,  NOW(), NOW()),
    (7::BIGINT,  75,  NOW(), NOW()),
    (8::BIGINT,  0,   NOW(), NOW()),
    (9::BIGINT,  160, NOW(), NOW()),
    (10::BIGINT, 45,  NOW(), NOW()),
    (11::BIGINT, 90,  NOW(), NOW())
) AS datos(producto_id, cantidad, fecha_creacion, fecha_modificacion)
WHERE NOT EXISTS (SELECT 1 FROM inventario);
