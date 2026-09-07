<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Carrito - Lila Joyería</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

<header class="encabezado">
    <div class="contenedor">
        <h1>Lila Joyería</h1>

        <nav class="menu">
            <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
            <a href="${pageContext.request.contextPath}/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/registro.jsp">Registro</a>
            <a href="${pageContext.request.contextPath}/carrito.jsp">Carrito</a>
        </nav>
    </div>
</header>

<main class="contenedor">

    <section class="carrito-seccion">

        <h2>Mi carrito</h2>

        <div class="tabla-contenedor">

            <table class="tabla-carrito">

                <thead>
                <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th>Acción</th>
                </tr>
                </thead>

                <tbody>

                <c:choose>

                    <c:when test="${not empty carrito}">
                        <c:forEach var="item" items="${carrito}">

                            <tr>
                                <td>
                                    <c:out value="${item.joya.nombre}" />
                                </td>

                                <td>
                                    $<c:out value="${item.joya.precio}" />
                                </td>

                                <td>
                                    <c:out value="${item.cantidad}" />
                                </td>

                                <td>
                                    $<c:out value="${item.precioUnitario}" />
                                </td>

                                <td>
                                    <button class="boton-eliminar">
                                        Eliminar
                                    </button>
                                </td>
                            </tr>

                        </c:forEach>
                    </c:when>

                    <c:otherwise>

                        <tr>
                            <td colspan="5">
                                <div class="mensaje-info">
                                    El carrito está vacío.
                                </div>
                            </td>
                        </tr>

                    </c:otherwise>

                </c:choose>

                </tbody>

            </table>

        </div>

        <div class="resumen-carrito">
            <h3>
                Total:
                $<c:out value="${empty totalCarrito ? 0 : totalCarrito}" />
            </h3>

            <button class="boton-formulario">
                Realizar pedido
            </button>
        </div>

    </section>

</main>

<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

</body>
</html>