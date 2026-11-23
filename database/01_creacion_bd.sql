-- ============================================================
-- Taller Automotriz - Proyecto Integrador Desarrollo Empresarial
-- Script 01: creacion de la base de datos
-- Motor: PostgreSQL 18
--
-- IMPORTANTE: ejecutar este script conectado a la base de datos
-- "postgres" desde pgAdmin 4 (Query Tool), no a taller_automotriz.
-- ============================================================

CREATE DATABASE taller_automotriz
    WITH ENCODING 'UTF8'
    TEMPLATE template0;

-- Verificacion
SELECT datname AS base_creada
FROM pg_database
WHERE datname = 'taller_automotriz';
