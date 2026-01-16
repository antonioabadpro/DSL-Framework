/* 
 * Script SQL para la creacion de la Base de Datos adaptado para Supabase (Postgree SQL)
 * Utilizamos el metodo de DROP & CREATE para que todos los datos esten disponibles de nuevo en cada ejecucion del programa
 */

/*
 * Creamos la Tabla de Bebidas Calientes
 */

DROP TABLE "BebidasCalientes";

CREATE TABLE "BebidasCalientes" (
  "name" VARCHAR(30) NOT NULL,
  "stock" INTEGER,
  PRIMARY KEY ("name")
);

INSERT INTO "BebidasCalientes" ("name", "stock") VALUES
('cafe', 6),
('chocolate', 6),
('te', 4);


/*
 * Creamos la Tabla de Bebidas Frias
 */

DROP TABLE "BebidasFrias";

CREATE TABLE "BebidasFrias" (
  "name" VARCHAR(30) NOT NULL,
  "stock" INTEGER,
  PRIMARY KEY ("name")
);

INSERT INTO "BebidasFrias" ("name", "stock") VALUES
('coca-cola', 6),
('pepsi', 6),
('nestea', 4);
