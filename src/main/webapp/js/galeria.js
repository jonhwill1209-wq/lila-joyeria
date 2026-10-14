/* Galería acordeón de la página de inicio (GSAP).
   Se mantiene en un archivo externo porque Facelets evaluaría como EL
   las secuencias ${...} y rompería con '&&' / '<' dentro de XHTML. */
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
