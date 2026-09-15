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
            <a href="${pageContext.request.contextPath}/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/registro">Registro</a>
            <a href="${pageContext.request.contextPath}/carrito">Carrito</a>
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

            <a href="${pageContext.request.contextPath}/catalogo" class="boton">
                Ver catálogo
            </a>
        </div>
    </section>


    <!-- SECCIÓN DE GALERÍA ACORDEÓN DE JOYERÍA -->
    <section class="catalogo-seccion">
        <div class="contenedor">
            <h2>Nuestras Colecciones</h2>
            <p class="catalogo-descripcion">Explora nuestra selección exclusiva de alta joyería.</p>

            <!-- Contenedor del Acordeón -->
            <!-- Contenedor del Acordeón con imágenes optimizadas -->
            <div class="accordion-gallery" id="joyas-accordion">

                <!-- Panel 1: Anillos -->
                <div class="ag-panel" tabindex="0">
        <span class="ag-panel__frame">
            <span class="ag-panel__media">
                <img src="${pageContext.request.contextPath}/img/anillo1.jpeg" alt="Anillos">
            </span>
            <span class="ag-panel__overlay"></span>
        </span>
                    <span class="ag-panel__label">
            <span class="ag-panel__bar"></span>
            <span class="ag-panel__text">Anillos de Compromiso</span>
        </span>
                </div>

                <!-- Panel 2: Collares -->
                <div class="ag-panel" tabindex="0">
        <span class="ag-panel__frame">
            <span class="ag-panel__media">
                <img src="${pageContext.request.contextPath}/img/collar1.jpeg" alt="Collares">
            </span>
            <span class="ag-panel__overlay"></span>
        </span>
                    <span class="ag-panel__label">
            <span class="ag-panel__bar"></span>
            <span class="ag-panel__text">Collares Elegantes</span>
        </span>
                </div>

                <!-- Panel 3: Aretes (Activo por defecto) -->
                <div class="ag-panel ag-panel--active" tabindex="0">
        <span class="ag-panel__frame">
            <span class="ag-panel__media">
                <img src="${pageContext.request.contextPath}/img/arete1.jpeg" alt="Aretes">
            </span>
            <span class="ag-panel__overlay"></span>
        </span>
                    <span class="ag-panel__label">
            <span class="ag-panel__bar"></span>
            <span class="ag-panel__text">Aretes de Diamante</span>
        </span>
                </div>

                <!-- Panel 4: Brazaletes -->
                <div class="ag-panel" tabindex="0">
        <span class="ag-panel__frame">
            <span class="ag-panel__media">
                <img src="${pageContext.request.contextPath}/img/pulsera1.jpeg" alt="Brazaletes">
            </span>
            <span class="ag-panel__overlay"></span>
        </span>
                    <span class="ag-panel__label">
            <span class="ag-panel__bar"></span>
            <span class="ag-panel__text">Brazaletes de Lujo</span>
        </span>
                </div>


                </div>

            </div>
        </div>
    </section>

    <!-- Librería GSAP  -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.2/gsap.min.js"></script>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const rootEl = document.getElementById('joyas-accordion');
            if (!rootEl) return;

            const panels = rootEl.querySelectorAll('.ag-panel');
            const count = panels.length;
            let activeIndex = 2; // Panel activo por defecto

            // Variables de configuración basadas en el original
            const tilt = 8;
            const duration = 0.6;
            const ease = 'power3.out';
            const expandRatio = 0.52;
            const parallax = 0.5;
            const gap = 12;

            let mediaSize = 320;
            let isVertical = false;

            // Calcula exactamente cuánto se expande el panel activo
            const grow = count > 1 ? (expandRatio * (count - 1)) / (1 - expandRatio) : 1;

            // Función que inyecta las animaciones GSAP
            function applyLayout(animate = true) {
                const tl = gsap.timeline();
                const dur = animate ? duration : 0;

                panels.forEach((panel, i) => {
                    const isActive = i === activeIndex;
                    const media = panel.querySelector('.ag-panel__media');
                    const bar = panel.querySelector('.ag-panel__bar');
                    const text = panel.querySelector('.ag-panel__text');

                    const rot = isActive ? 0 : (i < activeIndex ? tilt : -tilt);
                    // Si está en celular, rota en X, si está en PC rota en Y
                    const rotProp = isVertical ? { rotateX: -rot } : { rotateY: rot };

                    tl.to(panel, {
                        flexGrow: isActive ? grow : 1,
                        ...rotProp,
                        duration: dur,
                        ease: ease
                    }, 0);

                    if (media) {
                        const drift = Math.max(-1.5, Math.min(1.5, activeIndex - i));
                        const shift = drift * parallax * mediaSize * 0.06;

                        tl.to(media, {
                            xPercent: -50,
                            yPercent: -50,
                            x: isVertical ? 0 : (isActive ? 0 : shift),
                            y: isVertical ? (isActive ? 0 : shift) : 0,
                            '--ag-gray': isActive ? 0 : 1,
                            '--ag-dim': isActive ? 0 : 0.35,
                            duration: dur,
                            ease: ease
                        }, 0);
                    }

                    if (bar && text) {
                        if (isActive) {
                            tl.to([bar, text], {opacity: 1, x: 0, duration: dur, ease: ease, stagger: 0.06}, 0);
                        } else {
                            tl.to([bar, text], {opacity: 0, x: -14, duration: dur * 0.6, ease: ease}, 0);
                        }
                    }
                });
            }

            // Función que mide la pantalla y adapta el componente
            function measure() {
                isVertical = window.innerWidth <= 768;

                if (isVertical) {
                    rootEl.classList.add('accordion-gallery--vertical');
                } else {
                    rootEl.classList.remove('accordion-gallery--vertical');
                }

                const rect = rootEl.getBoundingClientRect();
                const total = isVertical ? rect.height : rect.width;
                const usable = Math.max(total - gap * (count - 1), 120);

                // El secreto del componente: Calcular el tamaño de la imagen dinámicamente
                mediaSize = Math.max(140, usable * Math.min(Math.max(expandRatio, 0.2), 0.9) * 1.22);
                rootEl.style.setProperty('--ag-media-size', `${mediaSize}px`);

                applyLayout(true);
            }

            // Eventos (Hover y Foco)
            panels.forEach((panel, i) => {
                panel.addEventListener('mouseenter', () => {
                    if (activeIndex !== i) {
                        activeIndex = i;
                        applyLayout(true);
                    }
                });
                panel.addEventListener('focus', () => {
                    if (activeIndex !== i) {
                        activeIndex = i;
                        applyLayout(true);
                    }
                });
            });

            // Iniciar
            measure();
            window.addEventListener('resize', measure);
        });
    </script>

</main>


<footer>
    <p>&copy; 2026 Lila Joyería</p>
</footer>

</body>
</html>
