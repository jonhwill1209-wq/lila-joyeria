<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Catálogo - Lila Joyería</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">
</head>

<body>

<header class="encabezado">
    <div class="contenedor">
        <h1>Lila Joyería</h1>

        <nav class="menu">
            <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
            <a href="${pageContext.request.contextPath}/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/registro">Registro</a>
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

        <c:if test="${not empty error}">
            <div class="mensaje-error">
                <c:out value="${error}" />
            </div>
        </c:if>

        <c:if test="${param.error == 'DatosInvalidos'}">
            <div class="mensaje-error">
                No fue posible agregar el producto al carrito.
            </div>
        </c:if>

        <c:if test="${param.error == 'JoyaNoEncontrada'}">
            <div class="mensaje-error">
                No se encontró la joya seleccionada en la base de datos.
            </div>
        </c:if>

        <c:if test="${param.error == 'ErrorAlAgregar'}">
            <div class="mensaje-error">
                Ocurrió un error al agregar la joya al carrito.
            </div>
        </c:if>

        <div class="lista-productos">

            <c:choose>

                <c:when test="${not empty joyas}">

                    <c:forEach var="joya" items="${joyas}">

                        <article class="producto">

                            <img src="${pageContext.request.contextPath}/img/productos/${joya.imagen}"
                                 alt="${joya.nombre}"
                                 class="producto-imagen">

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
                                <c:choose>
                                    <c:when test="${not empty joya.categoria}">
                                        <c:out value="${joya.categoria.nombre}" />
                                    </c:when>
                                    <c:otherwise>
                                        Sin categoría
                                    </c:otherwise>
                                </c:choose>
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

                            <c:choose>

                                <c:when test="${joya.stock > 0}">

                                    <form action="${pageContext.request.contextPath}/carrito"
                                          method="post">

                                        <input type="hidden"
                                               name="idJoya"
                                               value="${joya.idJoya}">

                                        <input type="hidden"
                                               name="cantidad"
                                               value="1">

                                        <button type="submit"
                                                class="boton-carrito">
                                            Agregar al carrito
                                        </button>

                                    </form>

                                </c:when>

                                <c:otherwise>
                                    <p>
                                        <strong>Producto agotado</strong>
                                    </p>
                                </c:otherwise>

                            </c:choose>

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