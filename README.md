# Sistema de Administración de Estacionamiento y Control de Accesos

Aplicación de escritorio desarrollada en **Java** con interfaz gráfica **JavaFX** y arquitectura **MVC (Modelo-Vista-Controlador)** para la gestión integral de estacionamientos[cite: 10, 11, 14]. El sistema automatiza el registro de entradas y salidas de vehículos, el control de ocupación en tiempo real, el cálculo parametrizable de tarifas, la emisión de tickets y la generación de reportes financieros y operativos[cite: 11, 13, 14].

---

## 📋 Descripción General del Proyecto

El sistema resuelve las ineficiencias operativas asociadas al control manual de estacionamientos, eliminando demoras en los puntos de acceso y errores en el cobro de tarifas[cite: 11].

### Características Principales
* **Control de Acceso y Roles:** Autenticación segura con contraseñas encriptadas mediante el algoritmo **SHA-1** y redirección según privilegios (*Administrador*, *Operador de Caseta* y *Auditor/Gerencia*).
* **Operación en Caseta:** Registro ágil de ingresos con asignación automática de folio y cajón, monitoreo de disponibilidad en tiempo real y validación de salidas[cite: 13, 14].
* **Cálculo de Tarifas Parametrizable:** Aplicación automática de cuotas por hora/fracción, periodos de tolerancia y recargos por boleto perdido[cite: 13, 14].
* **Auditoría y Reportes:** Visualización de métricas operativas, cortes de caja y gráficas estadísticas de flujo vehicular e ingresos[cite: 11, 13, 14].

### Arquitectura y Patrones Implementados
* **Arquitectura MVC:** Separación estricta entre la capa de datos (`model` y `dao`), lógica de interfaz (`controller`) y vistas (`view`)[cite: 16].
* **Programación Genérica (`<T>`):** Implementación de `GenericDAO<T>` para estandarizar las operaciones CRUD en la base de datos[cite: 16].
* **Patrones de Diseño:**
  * **Singleton:** Gestión de una única instancia de conexión JDBC (`ConexionDB`)[cite: 16].
  * **DAO (Data Access Object):** Encapsulamiento de consultas SQL y prevención de inyección SQL mediante `PreparedStatement`[cite: 16].
  * **Strategy:** Cálculo dinámico e intercambiable de tarifas según tipo de vehículo y reglas de estancia[cite: 16].
  * **Factory Method:** Generación dinámica de comprobantes y tickets[cite: 16].

---

## 👨‍💻 Autores e Información Académica

* **Autor:** Vicente Soto García[cite: 10]
* **Institución:** Instituto Tecnológico de Celaya[cite: 10]
* **Carrera:** Ingeniería en Sistemas Computacionales[cite: 10]
* **Materia:** Tópicos Avanzados de Programación (5° Semestre - Grupo B)[cite: 10]
* **Docente:** Grimaldo Aguayo Oscar[cite: 10]

---

## 🛠️ Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de contar con las siguientes herramientas instaladas:

1. **Java Development Kit (JDK):** Versión 17 o superior.
2. **JavaFX SDK:** Configurado en el proyecto o gestionado mediante Maven/Gradle.
3. **Motor de Base de Datos:** MySQL Server 8.0 o superior[cite: 15].
4. **Conector JDBC:** `mysql-connector-j` (v8.0+ o v9.x) agregado a las dependencias del proyecto[cite: 3, 4].
5. **IDE Recomendado:** IntelliJ IDEA o Eclipse.

---

## 🚀 Pasos para la Ejecución

### 1. Clonar el Repositorio
Abre una terminal y descarga el código fuente del proyecto:
```bash
git clone https://github.com/Vicentekn/ProyectoFinalTAP.git
cd ProyectoFinalTAP
