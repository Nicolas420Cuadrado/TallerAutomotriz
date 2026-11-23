-- ============================================================
-- Taller Automotriz - Proyecto Integrador Desarrollo Empresarial
-- Script 05: migracion del esquema actual al DBML oficial
-- Motor: PostgreSQL 18
--
-- Ejecutar UNA SOLA VEZ sobre una base creada con 02, 03 y 04
-- o sobre la base generada por Hibernate con ddl-auto=update.
--
-- Para una base nueva: ejecutar 02, 03, 04 y despues este script.
-- Los scripts 03 y 04 siguen usando el esquema anterior; no volver
-- a ejecutarlos despues de aplicar esta migracion.
--
-- Conserva los datos existentes:
--   - email -> correo
--   - clave -> password_hash
--   - estado_usuario -> activo
--   - id_rol -> rol_id
--   - nombre + apellido -> nombre (maximo 100 caracteres)
--   - documento se genera como valor provisional si no existe
--   - fecha_creacion se completa con now()
--
-- No ejecutar este script si la base ya fue migrada.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- Evitar una segunda ejecucion accidental.
-- Si la base ya fue migrada, se aborta sin dejar cambios.
-- ------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'roles'
          AND column_name = 'id'
    ) OR EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'usuarios'
          AND column_name = 'documento'
    ) THEN
        RAISE EXCEPTION 'La base ya fue migrada al DBML oficial. No se vuelve a ejecutar el script 05.';
    END IF;
END $$;

-- ------------------------------------------------------------
-- Tipos enumerados del DBML
-- ------------------------------------------------------------
DO $$
BEGIN
    CREATE TYPE rol_nombre AS ENUM ('ADMINISTRADOR', 'MECANICO');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$
BEGIN
    CREATE TYPE color_placa AS ENUM ('AMARILLA', 'BLANCA');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$
BEGIN
    CREATE TYPE tipo_reparacion AS ENUM (
        'MANTENIMIENTO_PREVENTIVO',
        'REPARACION_CORRECTIVA',
        'REVISION',
        'OTRO'
    );
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$
BEGIN
    CREATE TYPE estado_reparacion AS ENUM ('EN_PROCESO', 'TERMINADO', 'CANCELADO');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- ------------------------------------------------------------
-- Quitar todas las FK de las tablas a migrar.
-- Se hace por descubrimiento para soportar tanto los nombres
-- del script 02 como los nombres generados por Hibernate.
-- ------------------------------------------------------------
DO $$
DECLARE
    r record;
BEGIN
    FOR r IN
        SELECT conrelid::regclass AS tabla, conname
        FROM pg_constraint
        WHERE conrelid IN (
            SELECT oid
            FROM pg_class
            WHERE relnamespace = 'public'::regnamespace
              AND relname IN (
                  'roles',
                  'modelos',
                  'usuarios',
                  'vehiculos',
                  'repuestos',
                  'repuesto_modelo',
                  'modelos_repuestos',
                  'reparaciones'
              )
        )
          AND contype = 'f'
    LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.tabla, r.conname);
    END LOOP;
END $$;

-- ------------------------------------------------------------
-- Quitar PK, UNIQUE y CHECK de las tablas a migrar.
-- Las NOT NULL no se tocan; PostgreSQL las conserva.
-- ------------------------------------------------------------
DO $$
DECLARE
    r record;
BEGIN
    FOR r IN
        SELECT conrelid::regclass AS tabla, conname
        FROM pg_constraint
        WHERE conrelid IN (
            SELECT oid
            FROM pg_class
            WHERE relnamespace = 'public'::regnamespace
              AND relname IN (
                  'roles',
                  'modelos',
                  'usuarios',
                  'vehiculos',
                  'repuestos',
                  'repuesto_modelo',
                  'modelos_repuestos',
                  'reparaciones'
              )
        )
          AND contype IN ('p', 'u', 'c')
    LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.tabla, r.conname);
    END LOOP;
END $$;

-- ------------------------------------------------------------
-- Quitar indices adicionales que el DBML no declara
-- ------------------------------------------------------------
DROP INDEX IF EXISTS idx_repuesto_nombre;
DROP INDEX IF EXISTS idx_reparacion_estado;
DROP INDEX IF EXISTS idx_reparacion_mecanico;
DROP INDEX IF EXISTS idx_reparacion_fecha;

-- ------------------------------------------------------------
-- Usuarios: preparar documento, fecha y nombre completo
-- ------------------------------------------------------------
ALTER TABLE usuarios ADD COLUMN documento VARCHAR(20);
ALTER TABLE usuarios ADD COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT now();

UPDATE usuarios
SET documento = CASE
    WHEN email = 'admin@taller.com' THEN '1000000001'
    WHEN email = 'mecanico@taller.com' THEN '1000000002'
    ELSE left('USR-' || id_usuario::text, 20)
END;

UPDATE usuarios
SET nombre = left(trim(nombre || ' ' || apellido), 100);

ALTER TABLE usuarios ALTER COLUMN documento SET NOT NULL;

ALTER TABLE usuarios DROP COLUMN apellido;
ALTER TABLE usuarios DROP COLUMN username;

-- ------------------------------------------------------------
-- Renombrar columnas y tablas al DBML
-- ------------------------------------------------------------
ALTER TABLE roles RENAME COLUMN id_rol TO id;
ALTER TABLE roles RENAME COLUMN nombre_rol TO nombre;

ALTER TABLE modelos RENAME COLUMN id_modelo TO id;
ALTER TABLE modelos RENAME COLUMN nombre_modelo TO nombre;
ALTER TABLE modelos RENAME COLUMN estado_modelo TO activo;

ALTER TABLE usuarios RENAME COLUMN id_usuario TO id;
ALTER TABLE usuarios RENAME COLUMN email TO correo;
ALTER TABLE usuarios RENAME COLUMN clave TO password_hash;
ALTER TABLE usuarios RENAME COLUMN estado_usuario TO activo;
ALTER TABLE usuarios RENAME COLUMN id_rol TO rol_id;

ALTER TABLE vehiculos RENAME COLUMN id_vehiculo TO id;
ALTER TABLE vehiculos RENAME COLUMN id_modelo TO modelo_id;
ALTER TABLE vehiculos RENAME COLUMN estado_vehiculo TO activo;

ALTER TABLE repuestos RENAME COLUMN id_repuesto TO id;
ALTER TABLE repuestos RENAME COLUMN ean TO codigo_ean;
ALTER TABLE repuestos RENAME COLUMN estado_repuesto TO activo;

ALTER TABLE reparaciones RENAME COLUMN id_reparacion TO id;
ALTER TABLE reparaciones RENAME COLUMN id_vehiculo TO vehiculo_id;
ALTER TABLE reparaciones RENAME COLUMN id_usuario_mecanico TO mecanico_id;

ALTER TABLE repuesto_modelo RENAME TO modelos_repuestos;
ALTER TABLE modelos_repuestos RENAME COLUMN id_repuesto TO repuesto_id;
ALTER TABLE modelos_repuestos RENAME COLUMN id_modelo TO modelo_id;

-- ------------------------------------------------------------
-- Ajustar longitudes, tipos y defaults
-- ------------------------------------------------------------
ALTER TABLE roles ALTER COLUMN nombre TYPE rol_nombre
    USING nombre::rol_nombre;

ALTER TABLE modelos ALTER COLUMN marca TYPE VARCHAR(100);
ALTER TABLE modelos ALTER COLUMN nombre TYPE VARCHAR(100);
ALTER TABLE modelos ALTER COLUMN activo SET DEFAULT TRUE;

ALTER TABLE usuarios ALTER COLUMN nombre TYPE VARCHAR(100);
ALTER TABLE usuarios ALTER COLUMN correo TYPE VARCHAR(150);
ALTER TABLE usuarios ALTER COLUMN password_hash TYPE VARCHAR(255);
ALTER TABLE usuarios ALTER COLUMN activo SET DEFAULT TRUE;
ALTER TABLE usuarios ALTER COLUMN fecha_creacion SET DEFAULT now();

ALTER TABLE vehiculos ALTER COLUMN nombre_dueno TYPE VARCHAR(150);
ALTER TABLE vehiculos ALTER COLUMN color_placa TYPE color_placa
    USING color_placa::color_placa;
ALTER TABLE vehiculos ALTER COLUMN activo SET DEFAULT TRUE;

ALTER TABLE repuestos ALTER COLUMN nombre TYPE VARCHAR(150);
ALTER TABLE repuestos ALTER COLUMN descripcion TYPE TEXT;
ALTER TABLE repuestos ALTER COLUMN stock SET DEFAULT 0;
ALTER TABLE repuestos ALTER COLUMN activo SET DEFAULT TRUE;

ALTER TABLE reparaciones ALTER COLUMN tipo TYPE tipo_reparacion
    USING tipo::tipo_reparacion;
ALTER TABLE reparaciones ALTER COLUMN estado DROP DEFAULT;
ALTER TABLE reparaciones ALTER COLUMN estado TYPE estado_reparacion
    USING estado::estado_reparacion;
ALTER TABLE reparaciones ALTER COLUMN estado SET DEFAULT 'EN_PROCESO'::estado_reparacion;
ALTER TABLE reparaciones ALTER COLUMN observaciones TYPE TEXT;

-- ------------------------------------------------------------
-- Restricciones del DBML
-- ------------------------------------------------------------
ALTER TABLE roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);

ALTER TABLE roles
    ADD CONSTRAINT uk_roles_nombre UNIQUE (nombre);

ALTER TABLE modelos
    ADD CONSTRAINT pk_modelos PRIMARY KEY (id);

ALTER TABLE modelos
    ADD CONSTRAINT uk_modelos_marca_nombre UNIQUE (marca, nombre);

ALTER TABLE usuarios
    ADD CONSTRAINT pk_usuarios PRIMARY KEY (id);

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_documento UNIQUE (documento);

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_correo UNIQUE (correo);

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_rol
    FOREIGN KEY (rol_id) REFERENCES roles (id);

ALTER TABLE vehiculos
    ADD CONSTRAINT pk_vehiculos PRIMARY KEY (id);

ALTER TABLE vehiculos
    ADD CONSTRAINT uk_vehiculos_placa UNIQUE (placa);

ALTER TABLE vehiculos
    ADD CONSTRAINT fk_vehiculos_modelo
    FOREIGN KEY (modelo_id) REFERENCES modelos (id);

ALTER TABLE repuestos
    ADD CONSTRAINT pk_repuestos PRIMARY KEY (id);

ALTER TABLE repuestos
    ADD CONSTRAINT uk_repuestos_codigo_ean UNIQUE (codigo_ean);

ALTER TABLE modelos_repuestos
    ADD CONSTRAINT pk_modelos_repuestos PRIMARY KEY (modelo_id, repuesto_id);

ALTER TABLE modelos_repuestos
    ADD CONSTRAINT fk_modelos_repuestos_modelo
    FOREIGN KEY (modelo_id) REFERENCES modelos (id);

ALTER TABLE modelos_repuestos
    ADD CONSTRAINT fk_modelos_repuestos_repuesto
    FOREIGN KEY (repuesto_id) REFERENCES repuestos (id);

ALTER TABLE reparaciones
    ADD CONSTRAINT pk_reparaciones PRIMARY KEY (id);

ALTER TABLE reparaciones
    ADD CONSTRAINT fk_reparaciones_vehiculo
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id);

ALTER TABLE reparaciones
    ADD CONSTRAINT fk_reparaciones_mecanico
    FOREIGN KEY (mecanico_id) REFERENCES usuarios (id);

COMMIT;
