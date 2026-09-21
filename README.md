#  Lila Joyería - E-Commerce MVC

Bienvenido al repositorio oficial de **Lila Joyería**, una aplicación web de comercio electrónico desarrollada en Java aplicando el patrón de arquitectura **Modelo-Vista-Controlador (MVC)**.

Este proyecto ha sido desarrollado como parte de la asignatura **DESARROLLO DE APLICACIONES CON WEB FRAMEWORKS (DWF901)**.

##  Tecnologías Utilizadas

* **Backend:** Java 17, Servlets (Jakarta EE 10), JSP, JSTL.
* **Gestor de Dependencias:** Maven.
* **Base de Datos:** MySQL 8.0+.
* **Servidor Web:** Apache Tomcat 10.x.
* **Arquitectura:** Patrón MVC (Model - View - Controller).

---

##  Equipo de Desarrollo

*Nota: Al ser 4 integrantes, se han distribuido los 5 roles técnicos para cubrir toda la arquitectura.*

1. **Jonathan Wilfredo Bonilla Erazo (MM120518)** - *Arquitecto de Entorno y Base de Datos (DevOps & DBA)*
2. **Norma Susana García Galdamez (GG253588)** - *Desarrollador Modelo (POJOs)*
3. **Darwin Ezequiel Martínez Rosales (MR251336)** - *Especialista en Persistencia y Acceso a Datos (DAO / JDBC)*
4. **Francisco José Duran Crespín (DC253061)** - *Desarrollador de Controladores (Servlets) y Frontend (Vistas JSP)*

---

##  Requisitos Previos (Prerrequisitos)

Para ejecutar este proyecto en tu entorno local, asegúrate de tener instalado:
* **Java JDK 17** o superior.
* **Apache Tomcat 10+** (Configurado en el IDE).
* **MySQL Server 8+** y MySQL Workbench (o similar).
* IDE recomendado: **IntelliJ IDEA Ultimate** o **Eclipse Enterprise Java**.

---

##  Instalación y Ejecución (Paso a Paso)

### 1. Clonar el repositorio
```bash
git clone [https://github.com/jonhwill1209-wq/lila-joyeria.git](https://github.com/jonhwill1209-wq/lila-joyeria.git)
```

### 2. Configuración de la Base de Datos
1. Abre MySQL Workbench.
2. Ejecuta el script SQL ubicado en `recursos_db/lila_joyeria_schema.sql`.
   *(Este script creará la base de datos `lila_joyeria`, las tablas correspondientes y registros base).*

### 3. Configuración de Credenciales
1. Ve a la ruta `src/main/resources/database.properties`.
2. Edita el archivo con tus credenciales locales de MySQL:
```properties
db.url=jdbc:mysql://localhost:3306/lila_joyeria?useSSL=false&serverTimezone=UTC
db.user=tu_usuario_local
db.password=tu_contraseña_local
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Despliegue
1. Abre el proyecto en tu IDE como **Proyecto Maven** y actualiza las dependencias (el `pom.xml` descargará el conector de MySQL y las librerías Jakarta automáticamente).
2. Configura tu servidor **Apache Tomcat 10** apuntando al proyecto.
3. Ejecuta el servidor. La aplicación estará disponible por defecto en: `http://localhost:8080/lila-joyeria`

---

##  Flujo de Trabajo Git (Gitflow)

Este proyecto utiliza un flujo de trabajo basado en ramas para asegurar la trazabilidad del código y un proceso profesional:

* **`main`**: Rama de producción (Solo contiene código estable y evaluable).
* **`develop`**: Rama de integración (Todos los Pull Requests se hacen hacia esta rama).
* **`feature/*`**: Ramas de desarrollo individuales por cada integrante (ej. `feature/vistas-jsp`).

---

##  Estructura del Proyecto

```text
src/
 ├── main/
 │   ├── java/com/lilajoyeria/
 │   │   ├── controller/  # Servlets (Lógica de negocio y rutas)
 │   │   ├── dao/          # Data Access Objects (Conexión a BD)
 │   │   ├── model/       # Clases de dominio (POJOs)
 │   │   └── util/   # Configuración y conexión a BD
 │   ├── resources/
 │   │   └── database.properties # Credenciales DB
 │   └── webapp/           # Archivos JSP, CSS, JS, Imágenes
 └── recursos_db/          # Script de la base de datos (.sql)
```
