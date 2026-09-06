<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Catálogo - Lila Joyería</title>

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

    <section class="catalogo-seccion">

        <h2>Catálogo de Joyas</h2>

        <p class="catalogo-descripcion">
            Explora algunos de nuestros productos disponibles.
        </p>

        <div class="lista-productos">

            <c:choose>

                <c:when test="${not empty joyas}">
                    <c:forEach var="joya" items="${joyas}">

                        <article class="producto">

                            <h3>
                                <c:out value="${joya.nombre}" />
                            </h3>

                            <c:if test="${not empty joya.descripcion}">
                                <p>
                                    <c:out value="${joya.descripcion}" />
                                </p>
                            </c:if>

                            <p>
                                <strong>Categoría:</strong>
                                <c:out value="${joya.categoria.nombre}" />
                            </p>

                            <p>
                                <strong>Material:</strong>
                                <c:out value="${joya.material}" />
                            </p>

                            <c:if test="${not empty joya.quilates}">
                                <p>
                                    <strong>Quilates:</strong>
                                    <c:out value="${joya.quilates}" />
                                </p>
                            </c:if>

                            <p>
                                <strong>Precio:</strong>
                                $<c:out value="${joya.precio}" />
                            </p>

                            <p>
                                <strong>Stock:</strong>
                                <c:out value="${joya.stock}" /> unidades
                            </p>

                            <button class="boton-carrito">
                                Agregar al carrito
                            </button>

                        </article>

                    </c:forEach>
                </c:when>


                <c:otherwise>

                    <div class="mensaje-info">
                        No hay joyas disponibles en este momento.
                    </div>

                </c:otherwise>

            </c:choose>


        </div>

    </section>

</main>

<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

</body>
</html>