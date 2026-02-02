# DSL Framework - Guaraná Implementation ☕

![Java](https://img.shields.io/badge/Java-JDK%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![IDE](https://img.shields.io/badge/IDE-NetBeans%2022-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

Implementación de un Framework de Integración basado en el DSL **Guaraná** y los patrones de integración empresarial (EIP), desarrollado para la asignatura de **Integración de la Información y Aplicaciones (IIA)** de la Universidad de Huelva.

Este proyecto resuelve el problema de integración "CAFÉ", gestionando el flujo de pedidos desde archivos XML hasta su persistencia en base de datos y recomposición final.

## 🚀 Características Principales

* **Arquitectura Pipes & Filters:** Desacoplamiento total entre tareas para máxima reutilización.
* **Gestión de Estado Centralizada:** Uso de Singleton `Almacén` para patrones complejos como *Splitter* y *Aggregator*.
* **Conexión Empresarial:** Integración con **Supabase (PostgreSQL)** mediante un conector especializado que simula una API externa.
* **Estructuras de Datos:** Implementación de *Slots* como buffers intermedios para la transferencia de mensajes.

---

## 📦 Versiones del Proyecto

El repositorio cuenta con dos ramas principales que representan la evolución del sistema:

### v1.0 - Versión Secuencial (Rama: `main` / `v1.0`)
Implementación clásica donde el flujo se ejecuta paso a paso en un único hilo.
* **Ejecución:** Bajo demanda.
* **Funcionamiento:** El sistema solicita por consola el nombre del fichero XML a procesar (ej: `order1.xml`).
* **Flujo:** El mensaje atraviesa todas las tareas en cadena hasta llegar al final antes de procesar el siguiente.

### v2.0 - Versión Concurrente (Rama: `concurrent` / `v2.0`)
Evolución a una arquitectura asíncrona y reactiva para simular un entorno de producción real.
* **Arquitectura:** Multi-hilo (Producer-Consumer) utilizando `LinkedBlockingQueue`.
* **File Watcher:** Un "Conector Inteligente" monitoriza constantemente la carpeta `/src/Comandas`.
* **Funcionamiento:** Al copiar y pegar un archivo en la carpeta, el sistema lo detecta y lo procesa automáticamente en paralelo.

---

## 🛠️ Catálogo de Patrones EIP Implementados

El framework incluye la implementación de las siguientes tareas abstractas:

1.  **Splitter:** Divide el pedido `order` en múltiples bebidas `drink` usando XPath.
2.  **Distributor (Router):** Enruta las bebidas según contenido (Frías vs. Calientes).
3.  **Replicator (Multicast):** Duplica el mensaje para procesamiento paralelo (Traducción y Preservación).
4.  **Translator:** Transforma XML a consultas SQL mediante XSLT.
5.  **Context Enricher:** Enriquece el pedido con datos de stock obtenidos de la API externa (Supabase).
6.  **Merger:** Fusiona los flujos de bebidas frías y calientes procesadas.
7.  **Aggregator:** Recompone el pedido original agregando los fragmentos procesados y manteniendo el orden.
8.  **CorrelationIdSetter:** Barrera de sincronización para flujos paralelos.

---

## ⚙️ Instalación y Ejecución

### Requisitos previos
* Java JDK 8 o superior.
* Conexión a internet (para la conexión con Supabase).
* Maven (opcional, si se usa para dependencias).

### Instrucciones para v2.0 (Recomendada)

1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/antonioabadpro/DSL-Framework.git](https://github.com/antonioabadpro/DSL-Framework.git)
    git checkout v2.0
    ```
2.  Configurar la cadena de conexión a la BD en `ConectorBD.java` (si aplica).
3.  Ejecutar la clase `AppCorrelationIdSetter.java`.
4.  **Simulación:** Copia un archivo de la carpeta `src/Comandas_Backup` y pégalo en `src/Comandas`.
5.  Observar en la consola cómo se detecta, procesa y renombra el archivo a `.procesado`.

---

## 📂 Estructura del Problema CAFÉ

El flujo implementado sigue la siguiente lógica de negocio:

1.  **Input:** Lectura de `orderX.xml`.
2.  **Split:** División en bebidas individuales.
3.  **Routing:** Separación Frías / Calientes.
4.  **Enrichment:** Consulta a BD Supabase para verificar stock.
5.  **Aggregation:** Reconstrucción del XML final.
6.  **Output:** Escritura del resultado en disco.

---

## 👥 Autores

Equipo de Desarrollo:

* **Jaime Abad Quirós**
* **Antonio Abad Hernández Gálvez**
* **Agustín Rodríguez Aguilar**
* **Sergio Núñez Sierra**

GitHub: [@antonioabadpro](https://github.com/antonioabadpro) <br>
*Proyecto realizado para la Universidad de Huelva (UHU)*

---

## ©️​ Licencia y Derechos de Autor

**© 2025. Antonio Abad Hernández Gálvez**. <br>
Todos los derechos reservados.

Este proyecto es propiedad intelectual de su autor. <br>
El código se proporciona únicamente con fines de **consulta y demostración de portfolio**.

El código fuente de este proyecto es propiedad exclusiva de su autor. 
Se permite su visualización con fines educativos y de evaluación académica.

⛔ **Prohibido su uso:** Queda estrictamente prohibida su reproducción total o parcial, modificación, distribución o uso para fines comerciales o académicos por parte de terceros sin la autorización expresa y por escrito del autor.

Este proyecto es el resultado de una evaluación académica para la Universidad de Huelva. <br>
El plagio o uso indebido de este código en otros proyectos académicos será reportado.
