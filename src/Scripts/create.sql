/* 
 * Script SQL adaptado para Apache Derby
 * Author:  agustinrodriguez
 * Created: 12 nov 2025
 */

-- NOTA: La base de datos 'iia2025' se crea y gestiona
-- a través de la cadena de conexión JDBC.

-- Opcionalmente, puedes crear un esquema:
-- CREATE SCHEMA IIA2025;
-- SET SCHEMA 'IIA2025';


/*
 * Tabla BebidasCalientes
 */

-- NOTA: Es NORMAL que esta línea falle la PRIMERA VEZ que ejecutas
-- el script, porque la tabla aún no existe para ser borrada.
DROP TABLE "BebidasCalientes";

CREATE TABLE "BebidasCalientes" (
  "nombre" VARCHAR(30) NOT NULL,
  "stock" INTEGER,
  PRIMARY KEY ("nombre")
);

INSERT INTO "BebidasCalientes" ("nombre", "stock") VALUES
('cafe', 6),
('chocolate', 6),
('te', 4);


/*
 * Tabla BebidasFrias
 */

-- NOTA: Es NORMAL que esta línea falle la PRIMERA VEZ que ejecutas
-- el script, porque la tabla aún no existe para ser borrada.
DROP TABLE "BebidasFrias";

CREATE TABLE "BebidasFrias" (
  "nombre" VARCHAR(30) NOT NULL,
  "stock" INTEGER
);

INSERT INTO "BebidasFrias" ("nombre", "stock") VALUES
('cocacola', 6),
('pepsi', 6),
('nestea', 4);

-- Se elimina el COMMIT; final porque el IDE
-- (NetBeans) usa Autocommit por defecto.