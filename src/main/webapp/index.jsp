<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Lila Joyería</title>

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
            <a href="${pageContext.request.contextPath}/catalogo.xhtml">
                Catálogo
            </a>
            <a href="${pageContext.request.contextPath}/login.xhtml">
                Login
            </a>
            <a href="${pageContext.request.contextPath}/registro.xhtml">
                Registro
            </a>
            <a href="${pageContext.request.contextPath}/carrito.xhtml">
                Carrito
            </a>
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

            <a href="${pageContext.request.contextPath}/catalogo.xhtml"
               class="boton">
                Ver catálogo
            </a>

        </div>
    </section>

    <section class="catalogo-seccion">
        <div class="contenedor">

            <h2>Nuestras Colecciones</h2>

            <p class="catalogo-descripcion">
                Explora nuestra selección exclusiva de alta joyería.
            </p>

            <div class="accordion-gallery" id="joyas-accordion">

                <a class="ag-panel"
                   href="${pageContext.request.contextPath}/catalogo.xhtml">

                    <span class="ag-panel__frame">
                        <span class="ag-panel__media">
                            <img src="${pageContext.request.contextPath}/img/anillo1.jpeg"
                                 alt="Anillos">
                        </span>
                        <span class="ag-panel__overlay"></span>
                    </span>

                    <span class="ag-panel__label">
                        <span class="ag-panel__bar"></span>
                        <span class="ag-panel__text">
                            Anillos de Compromiso
                        </span>
                    </span>
                </a>

                <a class="ag-panel"
                   href="${pageContext.request.contextPath}/catalogo.xhtml">

                    <span class="ag-panel__frame">
                        <span class="ag-panel__media">
                            <img src="${pageContext.request.contextPath}/img/collar1.jpeg"
                                 alt="Collares">
                        </span>
                        <span class="ag-panel__overlay"></span>
                    </span>

                    <span class="ag-panel__label">
                        <span class="ag-panel__bar"></span>
                        <span class="ag-panel__text">
                            Collares Elegantes
                        </span>
                    </span>
                </a>

                <a class="ag-panel ag-panel--active"
                   href="${pageContext.request.contextPath}/catalogo.xhtml">

                    <span class="ag-panel__frame">
                        <span class="ag-panel__media">
                            <img src="${pageContext.request.contextPath}/img/arete1.jpeg"
                                 alt="Aretes">
                        </span>
                        <span class="ag-panel__overlay"></span>
                    </span>

                    <span class="ag-panel__label">
                        <span class="ag-panel__bar"></span>
                        <span class="ag-panel__text">
                            Aretes de Diamante
                        </span>
                    </span>
                </a>

                <a class="ag-panel"
                   href="${pageContext.request.contextPath}/catalogo.xhtml">

                    <span class="ag-panel__frame">
                        <span class="ag-panel__media">
                            <img src="${pageContext.request.contextPath}/img/pulsera1.jpeg"
                                 alt="Brazaletes">
                        </span>
                        <span class="ag-panel__overlay"></span>
                    </span>

                    <span class="ag-panel__label">
                        <span class="ag-panel__bar"></span>
                        <span class="ag-panel__text">
                            Brazaletes de Lujo
                        </span>
                    </span>
                </a>

            </div>
        </div>
    </section>

</main>

<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.2/gsap.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const rootEl = document.getElementById('joyas-accordion');

        if (!rootEl || typeof gsap === 'undefined') {
            return;
        }

        const panels = rootEl.querySelectorAll('.ag-panel');
        const count = panels.length;

        let activeIndex = 2;
        let mediaSize = 320;
        let isVertical = false;

        const tilt = 8;
        const duration = 0.6;
        const ease = 'power3.out';
        const expandRatio = 0.52;
        const parallax = 0.5;
        const gap = 12;

        const grow = count > 1
            ? (expandRatio * (count - 1)) / (1 - expandRatio)
            : 1;

        function applyLayout(animate = true) {
            const tl = gsap.timeline();
            const dur = animate ? duration : 0;

            panels.forEach((panel, i) => {
                const isActive = i === activeIndex;

                panel.classList.toggle('ag-panel--active', isActive);

                const media = panel.querySelector('.ag-panel__media');
                const bar = panel.querySelector('.ag-panel__bar');
                const label = panel.querySelector('.ag-panel__text');

                const rotation = isActive
                    ? 0
                    : (i < activeIndex ? tilt : -tilt);

                tl.to(panel, {
                    flexGrow: isActive ? grow : 1,
                    rotateX: isVertical ? -rotation : 0,
                    rotateY: isVertical ? 0 : rotation,
                    duration: dur,
                    ease: ease
                }, 0);

                if (media) {
                    const drift = Math.max(
                        -1.5,
                        Math.min(1.5, activeIndex - i)
                    );

                    const shift = drift * parallax * mediaSize * 0.06;

                    tl.to(media, {
                        xPercent: -50,
                        yPercent: -50,
                        x: isVertical || isActive ? 0 : shift,
                        y: isVertical && !isActive ? shift : 0,
                        '--ag-gray': isActive ? 0 : 1,
                        '--ag-dim': isActive ? 0 : 0.35,
                        duration: dur,
                        ease: ease
                    }, 0);
                }

                if (bar && label) {
                    tl.to([bar, label], {
                        opacity: isActive ? 1 : 0,
                        x: isActive ? 0 : -14,
                        duration: dur,
                        ease: ease
                    }, 0);
                }
            });
        }

        function measure() {
            isVertical = window.innerWidth <= 768;

            rootEl.classList.toggle(
                'accordion-gallery--vertical',
                isVertical
            );

            const rect = rootEl.getBoundingClientRect();
            const total = isVertical ? rect.height : rect.width;
            const usable = Math.max(total - gap * (count - 1), 120);

            mediaSize = Math.max(
                140,
                usable * Math.min(Math.max(expandRatio, 0.2), 0.9) * 1.22
            );

            rootEl.style.setProperty(
                '--ag-media-size',
                mediaSize + 'px'
            );

            applyLayout();
        }

        panels.forEach((panel, i) => {
            const activate = () => {
                if (activeIndex !== i) {
                    activeIndex = i;
                    applyLayout();
                }
            };

            panel.addEventListener('mouseenter', activate);
            panel.addEventListener('focus', activate);
        });

        measure();
        window.addEventListener('resize', measure);
    });
</script>

</body>
</html>