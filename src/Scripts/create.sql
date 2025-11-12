/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  agustinrodriguez
 * Created: 12 nov 2025
 */

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS `iia2025` DEFAULT CHARACTER SET utf8mb4 COLLATE
utf8mb4_0900_ai_ci;
USE `iia2025`;

DROP TABLE IF EXISTS `BebidasCalientes`;
CREATE TABLE IF NOT EXISTS `BebidasCalientes` (
  `nombre` varchar(30) NOT NULL,
  `stock` int(11) DEFAULT NULL,
  PRIMARY KEY (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `BebidasCalientes` (`nombre`, `stock`) VALUES
('cafe', 6),
('chocolate', 6),
('te', 4);

DROP TABLE IF EXISTS `BebidasFrias`;
CREATE TABLE IF NOT EXISTS `BebidasFrias` (
  `nombre` varchar(30) NOT NULL,
  `stock` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `bebidas_frias` (`nombre`, `stock`) VALUES
('cocacola', 6),
('pepsi', 6),
('nestea', 4);

COMMIT;