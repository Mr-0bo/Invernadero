-- MySQL dump 10.13  Distrib 8.0.45, for macos15 (arm64)
--
-- Host: localhost    Database: sistema_meteorologico_invernadero
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'acea8e2a-fab8-11f0-a1db-d4e81093b4cd:1-153';

--
-- Table structure for table `Alertas_Activas`
--

DROP TABLE IF EXISTS `Alertas_Activas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Alertas_Activas` (
  `id_alerta` bigint NOT NULL AUTO_INCREMENT,
  `id_registro` bigint NOT NULL,
  `tipo_alerta` varchar(50) NOT NULL,
  `mensaje` text NOT NULL,
  `fecha_hora_generada` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atendida` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_alerta`),
  KEY `id_registro` (`id_registro`),
  CONSTRAINT `alertas_activas_ibfk_1` FOREIGN KEY (`id_registro`) REFERENCES `Registros_Ambientales` (`id_registro`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Alertas_Activas`
--

LOCK TABLES `Alertas_Activas` WRITE;
/*!40000 ALTER TABLE `Alertas_Activas` DISABLE KEYS */;
/*!40000 ALTER TABLE `Alertas_Activas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Registros_Ambientales`
--

DROP TABLE IF EXISTS `Registros_Ambientales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Registros_Ambientales` (
  `id_registro` bigint NOT NULL AUTO_INCREMENT,
  `id_zona` int NOT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `temperatura_interior` decimal(4,2) NOT NULL,
  `temperatura_exterior` decimal(4,2) NOT NULL,
  `humedad_relativa` decimal(5,2) NOT NULL,
  `humedad_suelo` decimal(5,2) NOT NULL,
  `radiacion_solar` decimal(6,2) NOT NULL,
  `indice_uv` decimal(3,1) NOT NULL,
  `estado_ventilacion` varchar(20) NOT NULL,
  PRIMARY KEY (`id_registro`),
  KEY `id_zona` (`id_zona`),
  CONSTRAINT `registros_ambientales_ibfk_1` FOREIGN KEY (`id_zona`) REFERENCES `Zonas` (`id_zona`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Registros_Ambientales`
--

LOCK TABLES `Registros_Ambientales` WRITE;
/*!40000 ALTER TABLE `Registros_Ambientales` DISABLE KEYS */;
/*!40000 ALTER TABLE `Registros_Ambientales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Reglas_Negocio`
--

DROP TABLE IF EXISTS `Reglas_Negocio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Reglas_Negocio` (
  `id_regla` int NOT NULL AUTO_INCREMENT,
  `nombre_variable` varchar(50) NOT NULL,
  `valor_limite` decimal(6,2) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  PRIMARY KEY (`id_regla`),
  UNIQUE KEY `nombre_variable` (`nombre_variable`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reglas_Negocio`
--

LOCK TABLES `Reglas_Negocio` WRITE;
/*!40000 ALTER TABLE `Reglas_Negocio` DISABLE KEYS */;
INSERT INTO `Reglas_Negocio` VALUES (1,'temp_int_max',35.00,'Alerta si la temperatura interior supera los 35°C'),(2,'temp_ext_max',38.00,'Alerta si la temperatura exterior supera los 38°C'),(3,'hum_aire_min',40.00,'Alerta si la humedad del aire cae de 40%'),(4,'hum_suelo_min',30.00,'Alerta por estrés hídrico si el suelo cae de 30%'),(5,'uv_max',8.00,'Alerta si las condiciones UV superan el índice 8.0');
/*!40000 ALTER TABLE `Reglas_Negocio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Zonas`
--

DROP TABLE IF EXISTS `Zonas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Zonas` (
  `id_zona` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text,
  PRIMARY KEY (`id_zona`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Zonas`
--

LOCK TABLES `Zonas` WRITE;
/*!40000 ALTER TABLE `Zonas` DISABLE KEYS */;
INSERT INTO `Zonas` VALUES (1,'Zona Norte','Sección de hortalizas altas.'),(2,'Zona Sur','Área de plantas jóvenes.');
/*!40000 ALTER TABLE `Zonas` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-09 23:31:08
