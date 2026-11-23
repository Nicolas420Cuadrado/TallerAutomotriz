-- ============================================================
-- Taller Automotriz - Proyecto Integrador Desarrollo Empresarial
-- Script 04: datos de ejemplo para demostracion y capturas
-- Motor: PostgreSQL 18
--
-- Opcional. Ejecutar despues de 03_datos_iniciales.sql.
-- Los INSERT son idempotentes para evitar duplicados.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- Modelos
-- ------------------------------------------------------------
INSERT INTO modelos (marca, nombre_modelo)
VALUES ('Toyota', 'Corolla'),
       ('Mazda', '3'),
       ('Renault', 'Logan'),
       ('Chevrolet', 'Spark')
ON CONFLICT (marca, nombre_modelo) DO NOTHING;

-- ------------------------------------------------------------
-- Vehiculos
-- ------------------------------------------------------------
INSERT INTO vehiculos (placa, color_placa, nombre_dueno, documento_dueno, telefono_dueno, id_modelo)
SELECT 'ABC-123', 'AMARILLA', 'Juan Perez', '10203040', '3001234567', m.id_modelo
FROM modelos m
WHERE m.marca = 'Toyota' AND m.nombre_modelo = 'Corolla'
ON CONFLICT (placa) DO NOTHING;

INSERT INTO vehiculos (placa, color_placa, nombre_dueno, documento_dueno, telefono_dueno, id_modelo)
SELECT 'XYZ-987', 'BLANCA', 'Maria Gomez', '20304050', '3109876543', m.id_modelo
FROM modelos m
WHERE m.marca = 'Mazda' AND m.nombre_modelo = '3'
ON CONFLICT (placa) DO NOTHING;

INSERT INTO vehiculos (placa, color_placa, nombre_dueno, documento_dueno, telefono_dueno, id_modelo)
SELECT 'DEF-456', 'AMARILLA', 'Carlos Ruiz', '30405060', '3205557788', m.id_modelo
FROM modelos m
WHERE m.marca = 'Renault' AND m.nombre_modelo = 'Logan'
ON CONFLICT (placa) DO NOTHING;

-- ------------------------------------------------------------
-- Repuestos
-- ------------------------------------------------------------
INSERT INTO repuestos (nombre, descripcion, precio, ean, stock, imagen_url)
VALUES
    ('Filtro de aceite', 'Filtro de aceite para motor de gasolina', 35000.00, '1234567890123', 15, NULL),
    ('Pastillas de freno', 'Juego delantero de pastillas de freno', 120000.00, '2234567890123', 8, NULL),
    ('Bujia de encendido', 'Bujia de iridio', 28000.00, '3234567890123', 40, NULL),
    ('Aceite 5W30', 'Aceite sintetico para motor, presentacion 1 galon', 185000.00, '4234567890123', 12, NULL)
ON CONFLICT (ean) DO NOTHING;

-- ------------------------------------------------------------
-- Compatibilidad repuesto - modelo
-- ------------------------------------------------------------
INSERT INTO repuesto_modelo (id_repuesto, id_modelo)
SELECT r.id_repuesto, m.id_modelo
FROM repuestos r, modelos m
WHERE r.ean = '1234567890123'
  AND ((m.marca = 'Toyota' AND m.nombre_modelo = 'Corolla')
    OR (m.marca = 'Mazda' AND m.nombre_modelo = '3'))
ON CONFLICT DO NOTHING;

INSERT INTO repuesto_modelo (id_repuesto, id_modelo)
SELECT r.id_repuesto, m.id_modelo
FROM repuestos r, modelos m
WHERE r.ean = '2234567890123'
  AND ((m.marca = 'Toyota' AND m.nombre_modelo = 'Corolla')
    OR (m.marca = 'Renault' AND m.nombre_modelo = 'Logan'))
ON CONFLICT DO NOTHING;

INSERT INTO repuesto_modelo (id_repuesto, id_modelo)
SELECT r.id_repuesto, m.id_modelo
FROM repuestos r, modelos m
WHERE r.ean = '3234567890123'
ON CONFLICT DO NOTHING;

INSERT INTO repuesto_modelo (id_repuesto, id_modelo)
SELECT r.id_repuesto, m.id_modelo
FROM repuestos r, modelos m
WHERE r.ean = '4234567890123'
  AND ((m.marca = 'Toyota' AND m.nombre_modelo = 'Corolla')
    OR (m.marca = 'Chevrolet' AND m.nombre_modelo = 'Spark'))
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- Reparaciones de ejemplo (asignadas al usuario "mecanico")
-- ------------------------------------------------------------
INSERT INTO reparaciones (fecha, tipo, estado, id_vehiculo, id_usuario_mecanico, observaciones)
SELECT DATE '2026-11-02', 'MANTENIMIENTO_PREVENTIVO', 'EN_PROCESO',
       v.id_vehiculo, u.id_usuario, 'Cambio de aceite y filtro'
FROM vehiculos v, usuarios u
WHERE v.placa = 'ABC-123' AND u.username = 'mecanico'
  AND NOT EXISTS (
      SELECT 1 FROM reparaciones r
      WHERE r.id_vehiculo = v.id_vehiculo
        AND r.fecha = DATE '2026-11-02'
        AND r.tipo = 'MANTENIMIENTO_PREVENTIVO'
  );

INSERT INTO reparaciones (fecha, tipo, estado, id_vehiculo, id_usuario_mecanico, observaciones)
SELECT DATE '2026-10-28', 'REPARACION_CORRECTIVA', 'TERMINADO',
       v.id_vehiculo, u.id_usuario, 'Cambio de pastillas de freno'
FROM vehiculos v, usuarios u
WHERE v.placa = 'XYZ-987' AND u.username = 'mecanico'
  AND NOT EXISTS (
      SELECT 1 FROM reparaciones r
      WHERE r.id_vehiculo = v.id_vehiculo
        AND r.fecha = DATE '2026-10-28'
        AND r.tipo = 'REPARACION_CORRECTIVA'
  );

INSERT INTO reparaciones (fecha, tipo, estado, id_vehiculo, id_usuario_mecanico, observaciones)
SELECT DATE '2026-10-20', 'REVISION', 'CANCELADO',
       v.id_vehiculo, u.id_usuario, 'El cliente no autorizo la revision'
FROM vehiculos v, usuarios u
WHERE v.placa = 'DEF-456' AND u.username = 'mecanico'
  AND NOT EXISTS (
      SELECT 1 FROM reparaciones r
      WHERE r.id_vehiculo = v.id_vehiculo
        AND r.fecha = DATE '2026-10-20'
        AND r.tipo = 'REVISION'
  );

COMMIT;

-- Verificacion
SELECT 'modelos' AS tabla, COUNT(*) AS registros FROM modelos
UNION ALL SELECT 'vehiculos', COUNT(*) FROM vehiculos
UNION ALL SELECT 'repuestos', COUNT(*) FROM repuestos
UNION ALL SELECT 'repuesto_modelo', COUNT(*) FROM repuesto_modelo
UNION ALL SELECT 'reparaciones', COUNT(*) FROM reparaciones;
