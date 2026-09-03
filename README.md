# 💎 Lila Joyería - Sistema Web

Proyecto web para la gestión de productos y servicios de **Lila Joyería**, desarrollado en **Java (Jakarta EE)** con arquitectura **Maven**, servidor de aplicaciones **Apache Tomcat 10** y base de datos **MySQL**.

---

##  Requisitos Previos

Antes de comenzar con el despliegue local, asegúrate de contar con las siguientes herramientas instaladas:

* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Tomcat:** Versión 10.x.
* **Base de Datos:** Server MySQL y MySQL Workbench.
* **IDE:** IntelliJ IDEA, NetBeans o Eclipse con soporte para Maven.
* **Git:** Para el control de versiones.

---

##  Configuración e Instalación del Entorno

Sigue estos pasos para configurar y ejecutar el proyecto localmente por primera vez:

1. **Clonar el repositorio:**
   Ejecuta en tu terminal:
   `git clone [https://github.com/jonhwill1209-wq/lila-joyeria.git](https://github.com/jonhwill1209-wq/lila-joyeria.git)`

2. **Importar el proyecto:**
   * Abre tu IDE preferido.
   * Selecciona la opción **Open** / **Import Project**.
   * Selecciona la carpeta clonada y asegúrate de cargarlo como **Proyecto Maven** (`pom.xml`) para que se descarguen las dependencias necesarias.

3. **Configurar la Base de Datos:**
   * Abre **MySQL Workbench** y conéctate a tu servidor local.
   * Ejecuta el script SQL ubicado en la ruta: `recursos_db/lila_joyeria_schema.sql`
   * Verifica que se hayan creado la base de datos y las tablas correspondientes.

4. **Credenciales de Conexión:**
   * Dirígete a la ruta: `src/main/resources/database.properties`
   * Edita las propiedades del archivo para colocar tu usuario y contraseña local de MySQL.

5. **Configurar el Servidor y Desplegar:**
   * Agrega y configura un servidor **Apache Tomcat 10** en tu IDE.
   * Vincula el artefacto del proyecto (`lila-joyeria:war` o `lila-joyeria:war exploded`).
   * Inicia el servidor y accede en tu navegador (por defecto en `http://localhost:8080/lila-joyeria`).

---

##  Flujo de Trabajo con Git (Manejo de Ramas)

Para mantener la estabilidad del proyecto en la rama principal (`main`), todos los desarrollos, correcciones y nuevas funcionalidades deben trabajarse mediante **ramas secundarias**.

1. **Sincronizar la rama principal:**
   Antes de comenzar a trabajar en algo nuevo, asegúrate de tener la última versión del código:
   `git checkout main`
   `git pull origin main`

2. **Crear una nueva rama por tarea:**
   Crea y cámbiate a una rama descriptiva para la función que vas a implementar:
   `git checkout -b feature/nombre-de-la-tarea`
   *(Ejemplo: `git checkout -b feature/login-usuario`)*

3. **Guardar y subir tus cambios:**
   Una vez completados y probados tus cambios locales:
   `git add .`
   `git commit -m "Descripción de los cambios realizados"`
   `git push origin feature/nombre-de-la-tarea`

4. **Solicitar la integración (Pull Request):**
   * Ve al repositorio en GitHub: [https://github.com/jonhwill1209-wq/lila-joyeria](https://github.com/jonhwill1209-wq/lila-joyeria)
   * Crea un **Pull Request (PR)** desde tu rama hacia la rama `main`.
   * Solicita la revisión a un compañero de equipo antes de realizar el *merge*.
