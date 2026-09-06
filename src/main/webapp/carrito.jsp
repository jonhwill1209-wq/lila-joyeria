<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
            <a href="${pageContext.request.contextPath}/catalogo.jsp">Catálogo</a>
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

                <tr>
                    <td>Anillo Elegante</td>
                    <td>$120.00</td>

                    <td>
                        <input
                                type="number"
                                value="1"
                                min="1"
                                class="cantidad">
                    </td>

                    <td>$120.00</td>

                    <td>
                        <button class="boton-eliminar">
                            Eliminar
                        </button>
                    </td>
                </tr>

                <tr>
                    <td>Collar Clásico</td>
                    <td>$85.00</td>

                    <td>
                        <input
                                type="number"
                                value="1"
                                min="1"
                                class="cantidad">
                    </td>

                    <td>$85.00</td>

                    <td>
                        <button class="boton-eliminar">
                            Eliminar
                        </button>
                    </td>
                </tr>

                </tbody>

            </table>

        </div>

        <div class="resumen-carrito">
            <h3>Total: $205.00</h3>

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