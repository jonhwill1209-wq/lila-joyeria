"use strict";

// Encapsulado para no interferir con las variables de app.js.
(() => {
    const apiJoyas = new URL("../api/joyas", document.baseURI);

    const ui = {
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
        cancelar: document.getElementById("btnCancelarJoya")
    };

    let idEdicion = null;
    let consultaActiva = null;

    instalarEstilos();

    function instalarEstilos() {
        const estilo = document.createElement("style");

        estilo.textContent = `
            .gestion-preparacion {
                margin: 0 0 48px;
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

            .gestion-formulario[hidden] {
                display: none;
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

    function cancelarConsulta() {
        if (consultaActiva) {
            consultaActiva.abort();
            consultaActiva = null;
        }

        ui.botonConsultar.disabled = false;
        ui.botonConsultar.textContent = "Cargar para editar";
        ui.consultar.setAttribute("aria-busy", "false");
    }

    function copiarCategorias() {
        const seleccionAnterior = ui.categoria.value;

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

        const existeSeleccion = opciones.some(
            (opcion) => opcion.value === seleccionAnterior
        );

        ui.categoria.value = existeSeleccion
            ? seleccionAnterior
            : "";
    }

    function limpiarValidaciones() {
        for (const campo of ui.formulario.querySelectorAll(
            "input, select"
        )) {
            campo.setCustomValidity("");
        }
    }

    function abrirNuevaJoya() {
        cancelarConsulta();
        ocultarMensaje();

        idEdicion = null;

        ui.formulario.reset();
        limpiarValidaciones();
        copiarCategorias();

        ui.titulo.textContent = "Nueva joya";
        ui.stock.value = "0";
        ui.formulario.hidden = false;

        ui.nombre.focus();
    }

    function cerrarFormulario() {
        cancelarConsulta();
        ocultarMensaje();

        idEdicion = null;

        ui.formulario.reset();
        limpiarValidaciones();
        ui.formulario.hidden = true;

        ui.nueva.focus();
    }

    async function cargarParaEditar(evento) {
        evento.preventDefault();

        if (!ui.consultar.reportValidity()) {
            return;
        }

        const id = Number(ui.idConsultar.value);

        if (!Number.isSafeInteger(id) || id <= 0 ||
            id > 2147483647) {
            mostrarMensaje(
                "Introduce un identificador entero válido.",
                "error"
            );
            return;
        }

        cancelarConsulta();

        const controlador = new AbortController();
        consultaActiva = controlador;

        let tiempoAgotado = false;

        const tiempoLimite = setTimeout(() => {
            tiempoAgotado = true;
            controlador.abort();
        }, 15000);

        // Oculta cualquier edición anterior durante la consulta.
        idEdicion = null;
        ui.formulario.hidden = true;
        ui.botonConsultar.disabled = true;
        ui.botonConsultar.textContent = "Consultando…";
        ui.consultar.setAttribute("aria-busy", "true");

        mostrarMensaje("Consultando la joya…");

        try {
            const respuesta = await fetch(
                `${apiJoyas.href.replace(/\/$/, "")}/${id}`,
                {
                    method: "GET",
                    headers: {
                        Accept: "application/json"
                    },
                    cache: "no-store",
                    signal: controlador.signal
                }
            );

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
                throw new Error(
                    typeof cuerpo?.mensaje === "string"
                        ? cuerpo.mensaje
                        : `No se pudo consultar la joya (HTTP ${respuesta.status}).`
                );
            }

            const joya = cuerpo.datos;

            if (!joya || typeof joya !== "object" ||
                Array.isArray(joya) || Number(joya.idJoya) !== id) {
                throw new Error(
                    "La respuesta no contiene la joya solicitada."
                );
            }

            if (consultaActiva !== controlador) {
                return;
            }

            ui.formulario.reset();
            limpiarValidaciones();
            copiarCategorias();

            idEdicion = id;

            ui.titulo.textContent = `Editar joya #${id}`;
            ui.nombre.value = joya.nombre ?? "";
            ui.material.value = joya.material ?? "";
            ui.precio.value = joya.precio ?? "";
            ui.stock.value = joya.stock ?? "";

            const categoria = joya.categoria;
            const idCategoria = categoria?.idCategoria;

            if (idCategoria != null) {
                const valor = String(idCategoria);

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

            mostrarMensaje(
                "Datos cargados. Puedes modificarlos y validarlos; todavía no se guardarán."
            );

            ui.nombre.focus();
        } catch (error) {
            if (consultaActiva !== controlador) {
                return;
            }

            const mensaje = tiempoAgotado
                ? "La consulta tardó demasiado. Intenta nuevamente."
                : error instanceof TypeError
                    ? "No se pudo conectar con el servidor. Comprueba que Tomcat esté iniciado."
                    : error.message || "No se pudo consultar la joya.";

            mostrarMensaje(mensaje, "error");
        } finally {
            clearTimeout(tiempoLimite);

            if (consultaActiva === controlador) {
                consultaActiva = null;
                ui.botonConsultar.disabled = false;
                ui.botonConsultar.textContent = "Cargar para editar";
                ui.consultar.setAttribute("aria-busy", "false");
            }
        }
    }

    function validarFormulario(evento) {
        evento.preventDefault();

        limpiarValidaciones();

        const nombre = ui.nombre.value.trim();
        const material = ui.material.value.trim();
        const precio = Number(ui.precio.value);
        const stock = Number(ui.stock.value);
        const idCategoria = Number(ui.categoria.value);

        if (!nombre) {
            ui.nombre.setCustomValidity(
                "Escribe el nombre de la joya."
            );
        }

        if (!material) {
            ui.material.setCustomValidity(
                "Escribe el material de la joya."
            );
        }

        if (!ui.precio.value ||
            !Number.isFinite(precio) || precio <= 0) {
            ui.precio.setCustomValidity(
                "El precio debe ser mayor que cero."
            );
        }

        if (!ui.stock.value ||
            !Number.isInteger(stock) || stock < 0) {
            ui.stock.setCustomValidity(
                "El stock debe ser un número entero sin negativos."
            );
        }

        if (!Number.isInteger(idCategoria) || idCategoria <= 0) {
            ui.categoria.setCustomValidity(
                "Selecciona una categoría."
            );
        }

        if (!ui.formulario.reportValidity()) {
            mostrarMensaje(
                "Revisa los campos señalados del formulario.",
                "error"
            );
            return;
        }

        ui.nombre.value = nombre;
        ui.material.value = material;

        const operacion = idEdicion === null
            ? "la nueva joya"
            : `la joya #${idEdicion}`;

        mostrarMensaje(
            `Los datos de ${operacion} cumplen las validaciones del formulario. No se enviaron ni se guardaron cambios.`
        );

        // Aquí conectaremos POST o PUT cuando esté listo
        // el contrato definitivo y la autenticación de la API.
    }

    ui.nueva.addEventListener("click", abrirNuevaJoya);
    ui.cancelar.addEventListener("click", cerrarFormulario);
    ui.consultar.addEventListener("submit", cargarParaEditar);
    ui.formulario.addEventListener("submit", validarFormulario);

    // Permite corregir un campo después de una validación fallida.
    ui.formulario.addEventListener("input", (evento) => {
        if (typeof evento.target.setCustomValidity === "function") {
            evento.target.setCustomValidity("");
        }

        ocultarMensaje();
    });
})();