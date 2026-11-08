"use strict";

(() => {
    const apiBaseUrl = new URL(
        document.querySelector('meta[name="api-base-url"]').content,
        document.baseURI
    );
    const apiJoyas = new URL("joyas", apiBaseUrl);
    const apiSesion = new URL("auth/sesion", apiBaseUrl);
    const apiLogout = new URL("auth/logout", apiBaseUrl);

    const ui = {
        panel: document.querySelector(".gestion-preparacion"),
        nueva: document.getElementById("btnNuevaJoya"),
        consultar: document.getElementById("formConsultarEdicion"),
        idConsultar: document.getElementById("idConsultarJoya"),
        botonConsultar: document.getElementById("btnConsultarEdicion"),
        mensaje: document.getElementById("mensajeGestion"),
        formulario: document.getElementById("formJoya"),
        titulo: document.getElementById("tituloFormularioJoya"),
        nombre: document.getElementById("nombreJoya"),
        material: document.getElementById("materialJoya"),
        precio: document.getElementById("precioJoya"),
        stock: document.getElementById("stockJoya"),
        categoria: document.getElementById("categoriaJoya"),
        categoriasCatalogo: document.getElementById("categoria"),
        cancelar: document.getElementById("btnCancelarJoya"),
        guardar: document.querySelector("#formJoya button[type='submit']"),
        productos: document.getElementById("productos"),
        recargar: document.getElementById("btnRecargar"),
        navegacion: document.querySelector(
            'nav[aria-label="Navegación principal"]'
        )
    };

    let usuario = null;
    let idEdicion = null;
    let ocupado = false;

    ui.panel.hidden = true;
    ui.formulario.hidden = true;

    instalarEstilos();
    prepararTextos();

    const estadoSesion = document.createElement("span");
    estadoSesion.setAttribute("role", "status");
    estadoSesion.setAttribute("aria-live", "polite");

    const botonSalir = document.createElement("button");
    botonSalir.type = "button";
    botonSalir.className = "boton boton-secundario";
    botonSalir.textContent = "Cerrar sesión";
    botonSalir.hidden = true;

    ui.navegacion.append(
        document.createTextNode(" "),
        estadoSesion,
        document.createTextNode(" "),
        botonSalir
    );

    const enlaceLogin = ui.navegacion.querySelector(
        'a[href="login.html"]'
    );

    function instalarEstilos() {
        const estilo = document.createElement("style");

        estilo.textContent = `
            .gestion-preparacion {
                margin: 0 0 48px;
            }

            .gestion-preparacion[hidden],
            .gestion-formulario[hidden] {
                display: none !important;
            }

            .gestion-preparacion h2 {
                margin-top: 0;
            }

            .gestion-nota {
                padding: 12px 16px;
                border-left: 4px solid #906b89;
                background: #f0e6ef;
            }

            .gestion-acciones {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                margin-top: 20px;
            }

            .gestion-consulta {
                display: flex;
                align-items: flex-end;
                flex-wrap: wrap;
                gap: 16px;
                margin-top: 24px;
            }

            .gestion-consulta .campo,
            .gestion-formulario .campo {
                flex: initial;
            }

            .gestion-consulta .campo {
                flex: 1 1 240px;
            }

            .gestion-formulario {
                display: grid;
                gap: 20px;
                margin-top: 24px;
                padding-top: 20px;
                border-top: 1px solid #d6c4d3;
            }

            .gestion-formulario h3 {
                margin: 0;
            }

            .gestion-columnas {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .gestion-preparacion input,
            .gestion-preparacion select {
                box-sizing: border-box;
                width: 100%;
                min-width: 0;
                min-height: 46px;
                padding: 10px 12px;
                color: #4a2545;
                background: white;
                border: 1px solid #d6c4d3;
                border-radius: 6px;
                font: inherit;
            }

            .gestion-preparacion small {
                font-size: 13px;
            }

            @media (max-width: 600px) {
                .gestion-columnas {
                    grid-template-columns: 1fr;
                }

                .gestion-consulta > button,
                .gestion-acciones > button {
                    width: 100%;
                }
            }
        `;

        document.head.append(estilo);
    }

    function prepararTextos() {
        document.getElementById("tituloGestion").textContent =
            "Administrar joyas";

        ui.panel.querySelector(".gestion-nota").textContent =
            "Agrega joyas o consulta una por su identificador para editarla.";

        ui.nueva.textContent = "Agregar joya";
        ui.guardar.textContent = "Guardar joya";
    }

    function esAdmin() {
        return usuario?.rol === "ADMIN";
    }

    function mostrarMensaje(texto, tipo = "") {
        ui.mensaje.textContent = texto;
        ui.mensaje.className = tipo
            ? `mensaje ${tipo}`
            : "mensaje";
        ui.mensaje.hidden = false;
    }

    function ocultarMensaje() {
        ui.mensaje.hidden = true;
        ui.mensaje.textContent = "";
    }

    function establecerOcupado(activo) {
        ocupado = activo;

        for (const control of ui.panel.querySelectorAll(
            "button, input, select"
        )) {
            control.disabled = activo;
        }

        botonSalir.disabled = activo;
        ui.panel.setAttribute("aria-busy", String(activo));
    }

    function actualizarSesion(datos) {
        usuario = datos;
        ui.panel.hidden = !esAdmin();
        actualizarBotonesEliminar();

        if (enlaceLogin) {
            enlaceLogin.hidden = usuario !== null;
        }

        botonSalir.hidden = usuario === null;

        estadoSesion.textContent = usuario
            ? `${usuario.nombre || "Usuario"} · ${usuario.rol}`
            : "";

        if (!esAdmin()) {
            ui.formulario.hidden = true;
            idEdicion = null;
            ui.formulario.reset();
        }
    }

    async function solicitar(url, opciones = {}) {
        const controlador = new AbortController();
        let tiempoAgotado = false;

        const limite = setTimeout(() => {
            tiempoAgotado = true;
            controlador.abort();
        }, 15000);

        try {
            const respuesta = await fetch(url, {
                ...opciones,
                credentials: "include",
                cache: "no-store",
                signal: controlador.signal,
                headers: {
                    Accept: "application/json",
                    ...(opciones.body
                        ? { "Content-Type": "application/json" }
                        : {}),
                    ...opciones.headers
                }
            });

            if (respuesta.status === 204) {
                return {
                    exito: true,
                    mensaje: "Operación completada.",
                    datos: null
                };
            }

            let cuerpo;

            try {
                cuerpo = await respuesta.json();
            } catch (error) {
                if (controlador.signal.aborted) {
                    throw error;
                }

                throw new Error(
                    `El servidor no devolvió JSON válido (HTTP ${respuesta.status}).`
                );
            }

            if (!respuesta.ok || cuerpo?.exito !== true) {
                const error = new Error(
                    typeof cuerpo?.mensaje === "string"
                        ? cuerpo.mensaje
                        : `La operación falló (HTTP ${respuesta.status}).`
                );

                error.status = respuesta.status;
                throw error;
            }

            return cuerpo;
        } catch (error) {
            if (tiempoAgotado) {
                throw new Error(
                    opciones.method === "POST" ||
                    opciones.method === "PUT"
                        ? "No se recibió respuesta a tiempo. Actualiza el catálogo y comprueba si el cambio se guardó antes de repetirlo."
                        : "La consulta tardó demasiado. Intenta nuevamente."
                );
            }

            if (error instanceof TypeError) {
                throw new Error(
                    "No se pudo conectar con el servidor. Si estabas guardando, revisa el catálogo antes de repetir la operación."
                );
            }

            throw error;
        } finally {
            clearTimeout(limite);
        }
    }

    async function consultarSesion() {
        if (ocupado) {
            return;
        }

        try {
            const cuerpo = await solicitar(apiSesion);

            if (!cuerpo.datos ||
                typeof cuerpo.datos !== "object" ||
                Array.isArray(cuerpo.datos) ||
                typeof cuerpo.datos.rol !== "string") {
                throw new Error("La respuesta de sesión no es válida.");
            }

            actualizarSesion(cuerpo.datos);
        } catch (error) {
            actualizarSesion(null);

            if (error.status !== 401) {
                estadoSesion.textContent =
                    "No se pudo comprobar la sesión. Recarga para reintentar.";
            }
        }
    }

    function copiarCategorias() {
        const opciones = [
            new Option("Selecciona una categoría", "")
        ];

        for (const opcion of ui.categoriasCatalogo.options) {
            if (opcion.value) {
                opciones.push(
                    new Option(opcion.textContent, opcion.value)
                );
            }
        }

        ui.categoria.replaceChildren(...opciones);
    }

    function limpiarValidaciones() {
        for (const campo of ui.formulario.querySelectorAll(
            "input, select"
        )) {
            campo.setCustomValidity("");
        }
    }

    function abrirNuevaJoya() {
        if (ocupado || !esAdmin()) {
            return;
        }

        ocultarMensaje();
        idEdicion = null;

        ui.formulario.reset();
        limpiarValidaciones();
        copiarCategorias();

        ui.titulo.textContent = "Nueva joya";
        ui.stock.value = "0";
        ui.guardar.textContent = "Crear joya";
        ui.formulario.hidden = false;
        ui.nombre.focus();
    }

    function cerrarFormulario() {
        if (ocupado) {
            return;
        }

        ocultarMensaje();
        idEdicion = null;
        ui.formulario.reset();
        limpiarValidaciones();
        ui.formulario.hidden = true;
        ui.nueva.focus();
    }

    async function cargarParaEditar(evento) {
        evento.preventDefault();

        if (ocupado || !esAdmin() ||
            !ui.consultar.reportValidity()) {
            return;
        }

        const id = Number(ui.idConsultar.value);

        if (!Number.isInteger(id) || id <= 0 ||
            id > 2147483647) {
            mostrarMensaje(
                "Introduce un identificador entero válido.",
                "error"
            );
            return;
        }

        idEdicion = null;
        ui.formulario.hidden = true;
        establecerOcupado(true);
        ui.botonConsultar.textContent = "Consultando…";
        mostrarMensaje("Consultando la joya…");

        let cargada = false;

        try {
            const cuerpo = await solicitar(
                `${apiJoyas.href.replace(/\/$/, "")}/${id}`
            );

            const joya = cuerpo.datos;

            if (!joya || typeof joya !== "object" ||
                Array.isArray(joya) || Number(joya.idJoya) !== id) {
                throw new Error(
                    "La respuesta no contiene la joya solicitada."
                );
            }

            ui.formulario.reset();
            limpiarValidaciones();
            copiarCategorias();

            idEdicion = id;
            ui.titulo.textContent = `Editar joya #${id}`;
            ui.guardar.textContent = "Guardar cambios";

            ui.nombre.value = joya.nombre ?? "";
            ui.material.value = joya.material ?? "";
            ui.precio.value = joya.precio ?? "";
            ui.stock.value = joya.stock ?? "";

            const categoria = joya.categoria;

            if (categoria?.idCategoria != null) {
                const valor = String(categoria.idCategoria);

                const existe = [...ui.categoria.options].some(
                    (opcion) => opcion.value === valor
                );

                if (!existe) {
                    ui.categoria.add(
                        new Option(
                            categoria.nombre || `Categoría ${valor}`,
                            valor
                        )
                    );
                }

                ui.categoria.value = valor;
            }

            ui.formulario.hidden = false;
            mostrarMensaje("Datos cargados. Puedes editarlos.");
            cargada = true;
        } catch (error) {
            mostrarMensaje(error.message, "error");
        } finally {
            establecerOcupado(false);
            ui.botonConsultar.textContent = "Cargar para editar";

            if (cargada) {
                ui.nombre.focus();
            }
        }
    }

    function obtenerDatosValidos() {
        limpiarValidaciones();

        const nombre = ui.nombre.value.trim();
        const material = ui.material.value.trim();
        const precio = Number(ui.precio.value);
        const stock = Number(ui.stock.value);
        const idCategoria = Number(ui.categoria.value);

        if (!nombre || nombre.length > 100) {
            ui.nombre.setCustomValidity(
                "Escribe un nombre de hasta 100 caracteres."
            );
        }

        if (!material || material.length > 50) {
            ui.material.setCustomValidity(
                "Escribe un material de hasta 50 caracteres."
            );
        }

        if (!ui.precio.value || !Number.isFinite(precio) ||
            precio <= 0 || precio > 99999999.99) {
            ui.precio.setCustomValidity(
                "Introduce un precio válido mayor que cero."
            );
        }

        if (!ui.stock.value || !Number.isInteger(stock) ||
            stock < 0 || stock > 2147483647) {
            ui.stock.setCustomValidity(
                "El stock debe ser un entero sin negativos."
            );
        }

        if (!Number.isInteger(idCategoria) ||
            idCategoria <= 0 || idCategoria > 2147483647) {
            ui.categoria.setCustomValidity(
                "Selecciona una categoría válida."
            );
        }

        if (!ui.formulario.reportValidity()) {
            return null;
        }

        return {
            nombre,
            material,
            precio,
            stock,
            idCategoria
        };
    }

    async function guardarJoya(evento) {
        evento.preventDefault();

        if (ocupado || !esAdmin()) {
            return;
        }

        const datos = obtenerDatosValidos();

        if (!datos) {
            mostrarMensaje(
                "Revisa los campos señalados.",
                "error"
            );
            return;
        }

        const editando = idEdicion !== null;

        const url = editando
            ? `${apiJoyas.href.replace(/\/$/, "")}/${idEdicion}`
            : apiJoyas;

        const textoBoton = ui.guardar.textContent;

        establecerOcupado(true);
        ui.guardar.textContent = "Guardando…";
        mostrarMensaje("Guardando la joya…");

        try {
            const cuerpo = await solicitar(url, {
                method: editando ? "PUT" : "POST",
                body: JSON.stringify(datos)
            });

            idEdicion = null;
            ui.formulario.reset();
            ui.formulario.hidden = true;

            mostrarMensaje(
                cuerpo.mensaje || "Joya guardada correctamente.",
                "exito"
            );

            // Reutiliza la actualización del catálogo de app.js.
            if (!ui.recargar.disabled) {
                ui.recargar.click();
            }
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                await actualizarTrasRechazo(error);
            } else {
                mostrarMensaje(error.message, "error");
            }
        } finally {
            establecerOcupado(false);
            ui.guardar.textContent = textoBoton;
        }
    }

    async function actualizarTrasRechazo(error) {
        actualizarSesion(null);

        // Mensaje fuera de la sección que acabamos de ocultar.
        estadoSesion.textContent = error.status === 401
            ? "La sesión terminó. Inicia sesión nuevamente."
            : "No tienes permisos de administrador. Inicia sesión con una cuenta autorizada.";
    }

    function actualizarBotonesEliminar() {
        for (const boton of ui.productos.querySelectorAll(
            "[data-eliminar-joya]"
        )) {
            boton.hidden = !esAdmin();
        }
    }

    async function eliminarJoya(evento) {
        const boton = evento.target instanceof Element
            ? evento.target.closest("[data-eliminar-joya]")
            : null;
        if (!boton || ocupado || !esAdmin()) {
            return;
        }

        const id = Number(boton.dataset.eliminarJoya);
        if (!Number.isSafeInteger(id) || id <= 0) {
            mostrarMensaje("La joya no tiene un identificador válido.", "error");
            return;
        }

        if (!window.confirm(`¿Eliminar la joya #${id}? Esta acción no se puede deshacer.`)) {
            return;
        }

        establecerOcupado(true);
        boton.disabled = true;
        mostrarMensaje(`Eliminando la joya #${id}…`);

        try {
            await solicitar(`${apiJoyas.href.replace(/\/$/, "")}/${id}`, {
                method: "DELETE"
            });
            mostrarMensaje(`La joya #${id} fue eliminada.`, "exito");
            ui.recargar.click();
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                await actualizarTrasRechazo(error);
            } else {
                mostrarMensaje(error.message, "error");
            }
        } finally {
            boton.disabled = false;
            establecerOcupado(false);
        }
    }

    async function cerrarSesion() {
        if (ocupado) {
            return;
        }

        establecerOcupado(true);
        botonSalir.textContent = "Cerrando…";

        try {
            await solicitar(apiLogout, {
                method: "POST"
            });

            actualizarSesion(null);
            estadoSesion.textContent = "Sesión cerrada.";
        } catch (error) {
            estadoSesion.textContent =
                `No se pudo confirmar el cierre: ${error.message}`;
        } finally {
            establecerOcupado(false);
            botonSalir.textContent = "Cerrar sesión";
        }
    }

    ui.nueva.addEventListener("click", abrirNuevaJoya);
    ui.cancelar.addEventListener("click", cerrarFormulario);
    ui.consultar.addEventListener("submit", cargarParaEditar);
    ui.formulario.addEventListener("submit", guardarJoya);
    ui.productos.addEventListener("click", eliminarJoya);
    window.addEventListener("lila:catalogo-renderizado", actualizarBotonesEliminar);
    botonSalir.addEventListener("click", cerrarSesion);

    ui.formulario.addEventListener("input", (evento) => {
        if (typeof evento.target.setCustomValidity === "function") {
            evento.target.setCustomValidity("");
        }

        ocultarMensaje();
    });

    actualizarBotonesEliminar();

    // Revisa la sesión al volver desde otra pestaña.
    window.addEventListener("focus", consultarSesion);

    consultarSesion();
})();