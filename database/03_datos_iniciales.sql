-- ============================================================
-- Taller Automotriz - Proyecto Integrador Desarrollo Empresarial
-- Script 03: roles y usuarios iniciales
-- Motor: PostgreSQL 18
--
-- Las claves se almacenan con BCrypt:
--   admin     / admin123
--   mecanico  / mecanico123
-- Cambiar estas credenciales en un entorno real.
-- ============================================================

BEGIN;

INSERT INTO roles (nombre_rol)
VALUES ('ADMINISTRADOR'),
       ('MECANICO')
ON CONFLICT (nombre_rol) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, email, username, clave, estado_usuario, id_rol)
VALUES (
    'Administrador',
    'Sistema',
    'admin@taller.com',
    'admin',
    '$2a$10$Z3H9AY41Y5WSbEAJK4zZ/e3E0RS/FzuXp918/XHE/VVL84sg2/89i',
    TRUE,
    (SELECT id_rol FROM roles WHERE nombre_rol = 'ADMINISTRADOR')
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, email, username, clave, estado_usuario, id_rol)
VALUES (
    'Mecanico',
    'Taller',
    'mecanico@taller.com',
    'mecanico',
    '$2a$10$dRKxPqQca.RyGxz4SavNqumTY/6vxpYNy5EWVH3vENcgFM6a8byna',
    TRUE,
    (SELECT id_rol FROM roles WHERE nombre_rol = 'MECANICO')
)
ON CONFLICT (username) DO NOTHING;

COMMIT;

-- Verificacion
SELECT u.id_usuario, u.username, u.email, r.nombre_rol, u.estado_usuario
FROM usuarios u
JOIN roles r ON r.id_rol = u.id_rol
ORDER BY u.id_usuario;
