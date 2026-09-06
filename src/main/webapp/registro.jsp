<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Registro - Lila Joyería</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

<header class="encabezado">
    <div class="contenedor">
        <h1>Lila Joyería</h1>

        <nav class="menu">
            <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
            <a href="${pageContext.request.contextPath}/catalogo.jsp">Catálogo</a>
            <a href="${pageContext.request.contextPath}/registro.jsp">Registro</a>
            <a href="${pageContext.request.contextPath}/carrito.jsp">Carrito</a>
        </nav>
    </div>
</header>

<main class="contenedor">

    <section class="formulario-seccion">

        <h2>Crear cuenta</h2>

        <p>Completa tus datos para registrarte en Lila Joyería.</p>

        <c:if test="${not empty mensajeExito}">
            <div class="mensaje-exito">
                <c:out value="${mensajeExito}" />
            </div>
        </c:if>

        <c:if test="${not empty mensajeError}">
            <div class="mensaje-error">
                <c:out value="${mensajeError}" />
            </div>
        </c:if>

        <form action="#" method="post" class="formulario">

            <div class="grupo-formulario">
                <label for="nombre">Nombre completo</label>
                <input
                        type="text"
                        id="nombre"
                        name="nombre"
                        placeholder="Ingresa tu nombre completo"
                        required
                        minlength="3"
                        maxlength="100">
            </div>

            <div class="grupo-formulario">
                <label for="email">Correo electrónico</label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="ejemplo@correo.com"
                        required
                        maxlength="100">
            </div>

            <div class="grupo-formulario">
                <label for="password">Contraseña</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Ingresa tu contraseña"
                        required
                        minlength="6"
                        maxlength="255">
            </div>

            <div class="grupo-formulario">
                <label for="confirmarPassword">Confirmar contraseña</label>
                <input
                        type="password"
                        id="confirmarPassword"
                        name="confirmarPassword"
                        placeholder="Confirma tu contraseña"
                        required
                        minlength="6"
                        maxlength="255">
            </div>

            <button type="submit" class="boton-formulario">
                Registrarse
            </button>

        </form>

    </section>

</main>

<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

<script>
    const formulario = document.querySelector(".formulario");
    const password = document.getElementById("password");
    const confirmarPassword = document.getElementById("confirmarPassword");

    formulario.addEventListener("submit", function (event) {

        if (password.value !== confirmarPassword.value) {
            event.preventDefault();

            confirmarPassword.setCustomValidity(
                "Las contraseñas no coinciden"
            );

            confirmarPassword.reportValidity();
        } else {
            confirmarPassword.setCustomValidity("");
        }
    });

    confirmarPassword.addEventListener("input", function () {
        confirmarPassword.setCustomValidity("");
    });
</script>

</body>
</html>