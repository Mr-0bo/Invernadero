# NEXUS | Smart Microclimate Telemetry & Automation System

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/es/docs/Web/JavaScript)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![NASA API](https://img.shields.io/badge/NASA_POWER_API-Integrated-E31B23?style=for-the-badge&logo=nasa&logoColor=white)](https://power.larc.nasa.gov/)

**Nexus** es una plataforma de software industrial distribuida y de arquitectura desacoplada, diseñada específicamente para la ingesta, análisis predictivo y mitigación automatizada de riesgos microclimáticos en invernaderos de alta productividad. El sistema erradica por completo la latencia operativa analógica tradicional mediante un motor asíncrono de persistencia y un procesador secuencial de reglas de negocio en tiempo real.

---

## Arquitectura y Core Engine

El núcleo de la plataforma se rige por un patrón arquitectónico **Controller-Service-Repository**, garantizando un bajo acoplamiento y una alta cohesión en la capa de servicios distribuidos.

### Componentes Clave del Ecosistema

* **Edge Telemetry Simulation (NASA Playback Offset):** Ante la ausencia de sensores físicos de hardware locales y la latencia intrínseca en el procesamiento de datos satelitales (especialmente la irradiancia de onda corta), el sistema implementa un algoritmo de *reproducción cronológica sincronizada*. Se consumen los datos meteorológicos del proyecto **NASA POWER** con un offset dinámico de 15 días, empatando de forma asíncrona la estampa temporal de la Ciudad de México con las matrices JSON de la agencia espacial.
* **Motor Asíncrono Temporizado:** Mediante hilos de ejecución concurrentes controlados por las directivas de planificación de Spring Boot (`@Scheduled`), la plataforma realiza la ingesta telemétrica de forma optimizada una vez cada hora, mitigando la redundancia de datos y controlando el desbordamiento de la base de datos relacional.
* **Capa de Visualización Dinámica Avanzada:** El frontend se comunica de manera no bloqueante con la API REST local utilizando la API Fetch de JavaScript ES6. Utiliza **Chart.js** para renderizar promedios móviles, valores mínimos y picos máximos de radiación solar (W/m²), temperatura del aire y humedad en ventanas críticas de 24 horas.

---

## Reglas de Negocio Automatizadas

Antes de realizar el *Commit* definitivo en el motor de persistencia, cada vector analítico es interceptado y procesado por el motor de validación bajo estrictas reglas agronómicas:

* **RN-01 (Umbral Crítico de Hipertermia y Actuación Mecánica):** Si la temperatura interior inferida por el backend excede el límite crítico de **35.0 °C**, el sistema muta de forma inmediata el estado lógico de los actuadores de ventilación forzada a `"Encendido"`. De manera concurrente, se genera e inserta un ticket en la cola de incidencias clasificado como `[PELIGRO TÉRMICO]`.
* **RN-02 (Mitigación de Déficit Hídrico por Evapotranspiración):** Si la concentración de vapor de agua (humedad relativa) cae por debajo del umbral mínimo de **30.0 %**, el motor de reglas detona un evento autónomo catalogado como `[ESTRÉS HÍDRICO]`, emitiendo directivas críticas para la activación de nebulizadores locales.
* **RN-03 (Ciclo de Vida de Incidencias e Integridad de Auditoría):** Para cumplir con los estándares internacionales de trazabilidad de fallas, el sistema implementa un esquema de *Soft Delete*. Ninguna alerta puede ser eliminada físicamente de la base de datos; las incidencias persisten bloqueando los indicadores clave del Dashboard hasta que un usuario administrador realiza una verificación in situ y despacha una petición estructurada HTTP PUT para cambiar el estado a `"Atendida"`.

---

## Stack Tecnológico

### Backend
* **Lenguaje de Programación:** Java 17 LTS
* **Framework Core:** Spring Boot 3.x (Spring Web, Spring Data JPA)
* **Gestor de Dependencias:** Maven
* **Cliente HTTP:** RestTemplate (Ingesta RESTful de datos geoespaciales)

### Frontend
* **Estructura e Interfaces:** HTML5 Semántico
* **Motor de Estilos:** Tailwind CSS 3.x (Glassmorphism & Dashboard Layout)
* **Framework de Gráficas:** Chart.js (Line charts asíncronos con escalamiento dinámico)
* **Iconografía:** FontAwesome 6.4 (Renderizado dinámico de actuadores mecánicos)

### Base de Datos y Persistencia
* **Motor Relacional:** MySQL Server 8.0
* **Mapeo Objeto-Relacional (ORM):** Hibernate / JPA
* **Esquema de Datos:** 3ra Forma Normal (3FN) con restricciones de integridad referencial rígidas (`ON DELETE RESTRICT`).

---

## Estructura de la Célula de Desarrollo 

El diseño, modelado e implementación del sistema Nexus se ejecutó bajo el marco de trabajo ágil **Scrum**, distribuyendo las responsabilidades de ingeniería de la siguiente manera:

* **Full-Stack Architecture & Integration Services:**
  * **Mario Garcia Bonal** — *Core Engineer / Backend Architect* (Diseño de controladores REST, desarrollo del motor de reglas, inyección manual y orquestación asíncrona de la API de la NASA).
  * **Gabriel Omar Espinosa Acosta** — *Frontend & Data Visualization Engineer* (Desarrollo de la interfaz de usuario con Tailwind CSS, manipulación asíncrona del DOM y renderizado dinámico con Chart.js).

* **Data Engineering, Persistence & Database Administration (DBA):**
  * **Ernesto Camarillo Gonzales**
  * **Carlos Eduardo Flores Bautista**
  * *Responsabilidades:* Diseño del Modelo Entidad-Relación (MER), normalización de tablas, redacción del Diccionario de Datos técnico y despliegue físico del esquema relacional en el servidor MySQL local.

* **Systems Modeling & Structural Architecture:**
  * **Rafael Morales Mendez**
  * *Responsabilidades:* Modelado formal de procesos de software mediante diagramas estructurados avanzados, incluyendo Diagramas de Flujo de Datos (DFD Contexto, Nivel 0, Nivel 1) y Diagrama de Clases UML bajo notación Mermaid.

* **Business Intelligence & Agronomic Rules Engine Analysis:**
  * **Diego Molina Pelcastre**
  * **Iancarlo Sigler Kimsi**
  * *Responsabilidades:* Levantamiento e ingeniería de requerimientos, análisis de la problemática microclimática y modelado analítico de los umbrales de riesgo para la codificación de las reglas de negocio.

---

## Despliegue en Red Local e Infraestructura

El sistema está diseñado para operar de forma local en un entorno de servidor dedicado dentro de una red interna corporativa, permitiendo la administración y auditoría remota segura.

### Prerrequisitos del Servidor
1. Java Development Kit (JDK) 17 instalado.
2. MySQL Server 8.0 corriendo en el puerto por defecto (`3306`).
3. Acceso a internet en el servidor para el aprovisionamiento de datos satelitales.

### Instalar la Base de Datos
Ejecute el script de inicialización estructurado ubicado en la carpeta del repositorio:
```bash
mysql -u tu_usuario -p < DB/invernadero.sql
