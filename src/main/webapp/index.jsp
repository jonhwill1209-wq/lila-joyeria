<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Lila Joyería</title>

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

<main>

    <section class="banner">
        <div class="contenedor">
            <h2>Encuentra la joya perfecta para cada ocasión</h2>

            <p>
                Descubre nuestra colección de anillos, collares,
                pulseras y accesorios.
            </p>

            <a href="${pageContext.request.contextPath}/catalogo.jsp" class="boton">Ver catálogo</a>
        </div>
    </section>


    <section class="productos">
        <div class="contenedor">

            <h2>Nuestras Joyas</h2>

            <div class="lista-productos">

                <article class="producto">
                    <h3>Anillo Elegante</h3>
                    <p>Material: Oro</p>
                    <p>Precio: $120.00</p>
                    <button>Agregar al carrito</button>
                </article>

                <article class="producto">
                    <h3>Collar Clásico</h3>
                    <p>Material: Plata</p>
                    <p>Precio: $85.00</p>
                    <button>Agregar al carrito</button>
                </article>

                <article class="producto">
                    <h3>Pulsera Moderna</h3>
                    <p>Material: Plata</p>
                    <p>Precio: $60.00</p>
                    <button>Agregar al carrito</button>
                </article>

            </div>

        </div>
    </section>

</main>


<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

</body>
</html>
