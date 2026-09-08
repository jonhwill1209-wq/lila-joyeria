<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Iniciar sesión - Lila Joyería</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

<header class="encabezado">
    <div class="contenedor">

        <h1>Lila Joyería</h1>

        <nav class="menu">
            <a href="${pageContext.request.contextPath}/index.jsp">
                Inicio
            </a>

            <a href="${pageContext.request.contextPath}/catalogo">
                Catálogo
            </a>

            <a href="${pageContext.request.contextPath}/login">
                Login
            </a>

            <a href="${pageContext.request.contextPath}/registro.jsp">
                Registro
            </a>

            <a href="${pageContext.request.contextPath}/carrito.jsp">
                Carrito
            </a>
        </nav>

    </div>
</header>


<main class="contenedor">

    <section class="formulario-seccion">

        <h2>Iniciar sesión</h2>

        <p>
            Ingresa tus datos para acceder a tu cuenta.
        </p>


        <!-- Mensaje de error enviado por LoginServlet -->
        <c:if test="${not empty error}">
            <div class="mensaje-error">
                <c:out value="${error}" />
            </div>
        </c:if>


        <!-- Formulario de inicio de sesión -->
        <form action="${pageContext.request.contextPath}/login"
              method="post"
              class="formulario">


            <!-- Correo electrónico -->
            <div class="grupo-formulario">

                <label for="correo">
                    Correo electrónico
                </label>

                <input
                        type="email"
                        id="correo"
                        name="correo"
                        placeholder="ejemplo@correo.com"
                        required
                        maxlength="100">

            </div>


            <!-- Contraseña -->
            <div class="grupo-formulario">

                <label for="password">
                    Contraseña
                </label>

                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Ingresa tu contraseña"
                        required
                        maxlength="255">

            </div>


            <!-- Botón -->
            <button type="submit"
                    class="boton-formulario">

                Iniciar sesión

            </button>

        </form>

    </section>

</main>


<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

</body>

</html>