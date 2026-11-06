"use strict";

const API_JOYAS = new URL("../api/joyas", document.baseURI);

const elementos = {
    formulario: document.getElementById("formFiltros"),
    buscar: document.getElementById("buscar"),
    categoria: document.getElementById("categoria"),
    disponibles: document.getElementById("soloDisponibles"),
    recargar: document.getElementById("btnRecargar"),
    mensaje: document.getElementById("mensaje"),
    carga: document.getElementById("estadoCarga"),
    productos: document.getElementById("productos"),
    contador: document.getElementById("contador"),
    sinResultados: document.getElementById("sinResultados")
};

const moneda = new Intl.NumberFormat("es-SV", {
    style: "currency",
    currency: "USD"
});

let joyas = [];
let cargando = false;
let cargaCorrecta = false;
let controladorDetalle = null;

// Ventana para consultar una joya mediante GET /api/joyas/{id}.
const detalle = crearVentanaDetalle();

function mostrarMensaje(texto, tipo = "error") {
    elementos.mensaje.textContent = texto;
    elementos.mensaje.className = `mensaje ${tipo}`;
    elementos.mensaje.hidden = false;
}

function ocultarMensaje() {
    elementos.mensaje.hidden = true;
    elementos.mensaje.textContent = "";
}

function establecerCarga(activa) {
    cargando = activa;
    elementos.carga.hidden = !activa;
    elementos.recargar.disabled = activa;
    elementos.recargar.textContent = activa
        ? "Cargando…"
        : "Actualizar catálogo";

    elementos.productos.setAttribute("aria-busy", String(activa));
}

function normalizar(valor) {
    return String(valor ?? "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase()
        .trim();
}

function crearTexto(etiqueta, clase, texto) {
    const elemento = document.createElement(etiqueta);
    elemento.className = clase;
    elemento.textContent = texto;
    return elemento;
}

function esObjeto(valor) {
    return valor !== null &&
        typeof valor === "object" &&
        !Array.isArray(valor);
}

function crearImagen(joya) {
    const contenedor = document.createElement("div");
    contenedor.className = "tarjeta-imagen";

    const alternativa = crearTexto(
        "span",
        "imagen-alternativa",
        "Imagen no disponible"
    );

    const archivo = String(joya.imagen ?? "").trim();

    if (!archivo) {
        contenedor.append(alternativa);
        return contenedor;
    }

    const imagen = document.createElement("img");
    imagen.alt = String(joya.nombre ?? "Joya");
    imagen.loading = "lazy";

    imagen.addEventListener("error", () => {
        contenedor.replaceChildren(alternativa);
    }, { once: true });

    imagen.src = new URL(
        `../img/${encodeURIComponent(archivo)}`,
        document.baseURI
    ).href;

    contenedor.append(imagen);
    return contenedor;
}

function crearTarjeta(joya, incluirBoton = true) {
    const tarjeta = document.createElement("article");
    tarjeta.className = "tarjeta";

    const contenido = document.createElement("div");
    contenido.className = "tarjeta-contenido";

    contenido.append(
        crearTexto(
            "p",
            "tarjeta-categoria",
            joya.categoria?.nombre || "Sin categoría"
        ),
        crearTexto("h3", "", joya.nombre || "Joya sin nombre"),
        crearTexto(
            "p",
            "tarjeta-descripcion",
            joya.descripcion || "Sin descripción."
        ),
        crearTexto(
            "p",
            "tarjeta-material",
            `Material: ${joya.material || "No especificado"}`
        )
    );

    if (!incluirBoton && joya.quilates != null) {
        contenido.append(
            crearTexto(
                "p",
                "tarjeta-material",
                `Quilates: ${joya.quilates}`
            )
        );
    }

    const precio = Number(joya.precio);

    contenido.append(
        crearTexto(
            "p",
            "tarjeta-precio",
            joya.precio != null && Number.isFinite(precio)
                ? moneda.format(precio)
                : "Precio no disponible"
        )
    );

    const stock = Number(joya.stock);
    const disponible = Number.isFinite(stock) && stock > 0;

    contenido.append(
        crearTexto(
            "span",
            disponible ? "stock" : "stock agotado",
            disponible ? `Disponibles: ${stock}` : "Agotado"
        )
    );

    if (incluirBoton) {
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "boton-formulario boton-detalle-rest";
        boton.textContent = "Ver detalle";
        boton.setAttribute(
            "aria-label",
            `Ver detalle de ${joya.nombre || "esta joya"}`
        );

        boton.addEventListener("click", () => {
            consultarDetalle(joya.idJoya);
        });

        contenido.append(boton);
    }

    tarjeta.append(crearImagen(joya), contenido);
    return tarjeta;
}

function cargarCategorias() {
    const seleccionAnterior = elementos.categoria.value;
    const categorias = new Map();

    for (const joya of joyas) {
        const categoria = joya.categoria;

        if (categoria?.idCategoria != null) {
            categorias.set(
                String(categoria.idCategoria),
                categoria.nombre || "Sin nombre"
            );
        }
    }

    elementos.categoria.replaceChildren(
        new Option("Todas las categorías", "")
    );

    const ordenadas = [...categorias.entries()].sort(
        (a, b) => a[1].localeCompare(b[1], "es")
    );

    for (const [id, nombre] of ordenadas) {
        elementos.categoria.add(new Option(nombre, id));
    }

    elementos.categoria.value = categorias.has(seleccionAnterior)
        ? seleccionAnterior
        : "";
}

function aplicarFiltros() {
    if (cargando || !cargaCorrecta) {
        return;
    }

    const busqueda = normalizar(elementos.buscar.value);
    const categoria = elementos.categoria.value;
    const soloDisponibles = elementos.disponibles.checked;

    const filtradas = joyas.filter((joya) => {
        const texto = normalizar(
            `${joya.nombre ?? ""} ${joya.material ?? ""} ${
                joya.descripcion ?? ""
            }`
        );

        const coincideTexto = texto.includes(busqueda);

        const coincideCategoria = !categoria ||
            String(joya.categoria?.idCategoria) === categoria;

        const coincideStock = !soloDisponibles ||
            Number(joya.stock) > 0;

        return coincideTexto && coincideCategoria && coincideStock;
    });

    const fragmento = document.createDocumentFragment();

    for (const joya of filtradas) {
        fragmento.append(crearTarjeta(joya));
    }

    elementos.productos.replaceChildren(fragmento);
    elementos.contador.textContent =
        `${filtradas.length} de ${joyas.length} joyas`;

    elementos.sinResultados.textContent = joyas.length === 0
        ? "Todavía no hay joyas registradas."
        : "No hay joyas que coincidan con los filtros seleccionados.";

    elementos.sinResultados.hidden = filtradas.length !== 0;
}

// Comprueba el formato común ApiResponse:
// { exito, mensaje, datos }.
async function leerRespuesta(respuesta, signal) {
    let cuerpo;

    try {
        cuerpo = await respuesta.json();
    } catch (error) {
        if (signal.aborted) {
            throw error;
        }

        throw new Error(
            `El servidor no devolvió JSON válido (HTTP ${
                respuesta.status
            }).`
        );
    }

    if (!respuesta.ok || cuerpo?.exito !== true) {
        const mensajeServidor =
            typeof cuerpo?.mensaje === "string"
                ? cuerpo.mensaje.trim()
                : "";

        throw new Error(
            mensajeServidor ||
            `No se pudo completar la consulta (HTTP ${
                respuesta.status
            }).`
        );
    }

    return cuerpo.datos;
}

function obtenerMensajeError(error, tiempoAgotado, esDetalle = false) {
    if (tiempoAgotado) {
        return esDetalle
            ? "La consulta del detalle tardó demasiado. Cierra la ventana e intenta nuevamente."
            : "La consulta tardó demasiado. Intenta actualizar el catálogo.";
    }

    if (error instanceof TypeError) {
        return "No se pudo conectar con el servidor. Comprueba que Tomcat esté iniciado.";
    }

    return error.message || "Ocurrió un error al consultar las joyas.";
}

async function cargarJoyas() {
    if (cargando) {
        return;
    }

    ocultarMensaje();
    cargaCorrecta = false;
    establecerCarga(true);

    elementos.productos.replaceChildren();
    elementos.contador.textContent = "";
    elementos.sinResultados.hidden = true;

    const controlador = new AbortController();
    let tiempoAgotado = false;

    const tiempoLimite = setTimeout(() => {
        tiempoAgotado = true;
        controlador.abort();
    }, 15000);

    try {
        const respuesta = await fetch(API_JOYAS, {
            method: "GET",
            headers: {
                Accept: "application/json"
            },
            cache: "no-store",
            signal: controlador.signal
        });

        const datos = await leerRespuesta(
            respuesta,
            controlador.signal
        );

        if (!Array.isArray(datos)) {
            throw new Error(
                "La respuesta del catálogo no contiene una lista de joyas."
            );
        }

        if (datos.some((joya) => !esObjeto(joya))) {
            throw new Error(
                "El catálogo contiene productos inválidos."
            );
        }

        joyas = datos;
        cargarCategorias();
        cargaCorrecta = true;
    } catch (error) {
        joyas = [];

        mostrarMensaje(
            obtenerMensajeError(error, tiempoAgotado)
        );
    } finally {
        clearTimeout(tiempoLimite);
        establecerCarga(false);

        if (cargaCorrecta) {
            aplicarFiltros();
        }
    }
}

function crearVentanaDetalle() {
    const estilos = document.createElement("style");

    estilos.textContent = `
        .boton-detalle-rest {
            display: block;
            margin: 20px auto 0;
            cursor: pointer;
        }

        .detalle-rest {
            box-sizing: border-box;
            width: min(540px, calc(100% - 32px));
            max-height: 90vh;
            margin: auto;
            padding: 24px;
            overflow-y: auto;
            border: 1px solid #D6C4D3;
            border-radius: 12px;
            background: #F6EAF4;
            color: #4A2545;
            font-family: inherit;
            box-shadow: 0 16px 48px rgba(51, 26, 56, 0.25);
        }

        .detalle-rest::backdrop {
            background: rgba(30, 15, 32, 0.6);
        }

        .detalle-rest-encabezado {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 20px;
        }

        .detalle-rest-encabezado h2 {
            margin: 0;
            font-size: 26px;
        }

        .detalle-rest-cerrar {
            padding: 8px 12px;
            border: 1px solid #4A2545;
            border-radius: 4px;
            background: white;
            color: #4A2545;
            font: inherit;
            cursor: pointer;
        }

        .detalle-rest-cerrar:focus-visible,
        .boton-detalle-rest:focus-visible {
            outline: 3px solid #94708E;
            outline-offset: 3px;
        }

        .detalle-rest .tarjeta {
            margin: 0;
        }

        .detalle-rest .tarjeta-imagen img {
            max-width: 100%;
        }

        .detalle-rest-estado {
            margin: 0 0 16px;
            line-height: 1.6;
        }

        .detalle-rest-error {
            padding: 12px;
            border: 1px solid #E2B6BE;
            border-radius: 6px;
            background: #F8E5E9;
            color: #8B2137;
        }
    `;

    document.head.append(estilos);

    const ventana = document.createElement("dialog");
    ventana.className = "detalle-rest";
    ventana.setAttribute("aria-labelledby", "tituloDetalleRest");

    const encabezado = document.createElement("div");
    encabezado.className = "detalle-rest-encabezado";

    const titulo = crearTexto("h2", "", "Detalle de la joya");
    titulo.id = "tituloDetalleRest";

    const cerrar = document.createElement("button");
    cerrar.type = "button";
    cerrar.className = "detalle-rest-cerrar";
    cerrar.textContent = "Cerrar";
    cerrar.autofocus = true;

    const estado = crearTexto("p", "detalle-rest-estado", "");
    estado.setAttribute("role", "status");
    estado.setAttribute("aria-live", "polite");

    const contenido = document.createElement("div");

    encabezado.append(titulo, cerrar);
    ventana.append(encabezado, estado, contenido);
    document.body.append(ventana);

    function cancelarConsulta() {
        if (controladorDetalle) {
            controladorDetalle.abort();
            controladorDetalle = null;
        }
    }

    cerrar.addEventListener("click", () => {
        cancelarConsulta();
        ventana.close();
    });

    // La tecla Escape también cancela la consulta pendiente.
    ventana.addEventListener("cancel", cancelarConsulta);

    return {
        ventana,
        estado,
        contenido
    };
}

async function consultarDetalle(idJoya) {
    const id = Number(idJoya);

    if (!Number.isSafeInteger(id) || id <= 0) {
        mostrarMensaje(
            "La joya no tiene un identificador válido para consultar su detalle."
        );
        return;
    }

    if (controladorDetalle) {
        controladorDetalle.abort();
    }

    const controlador = new AbortController();
    controladorDetalle = controlador;

    detalle.contenido.replaceChildren();
    detalle.contenido.setAttribute("aria-busy", "true");
    detalle.estado.className = "detalle-rest-estado";
    detalle.estado.textContent = "Consultando información de la joya…";

    if (!detalle.ventana.open) {
        detalle.ventana.showModal();
    }

    let tiempoAgotado = false;

    const tiempoLimite = setTimeout(() => {
        tiempoAgotado = true;
        controlador.abort();
    }, 15000);

    try {
        const url = new URL(
            `${API_JOYAS.href.replace(/\/$/, "")}/${id}`
        );

        const respuesta = await fetch(url, {
            method: "GET",
            headers: {
                Accept: "application/json"
            },
            cache: "no-store",
            signal: controlador.signal
        });

        const joya = await leerRespuesta(
            respuesta,
            controlador.signal
        );

        if (!esObjeto(joya) || Number(joya.idJoya) !== id) {
            throw new Error(
                "El servidor no devolvió los datos de la joya solicitada."
            );
        }

        // Descarta respuestas de consultas cerradas o reemplazadas.
        if (controladorDetalle !== controlador) {
            return;
        }

        detalle.contenido.replaceChildren(
            crearTarjeta(joya, false)
        );

        detalle.estado.textContent =
            "Información actualizada desde el servidor.";
    } catch (error) {
        if (controladorDetalle !== controlador) {
            return;
        }

        detalle.estado.className =
            "detalle-rest-estado detalle-rest-error";

        detalle.estado.textContent = obtenerMensajeError(
            error,
            tiempoAgotado,
            true
        );
    } finally {
        clearTimeout(tiempoLimite);

        if (controladorDetalle === controlador) {
            controladorDetalle = null;
            detalle.contenido.setAttribute("aria-busy", "false");
        }
    }
}

elementos.formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    aplicarFiltros();
});

elementos.buscar.addEventListener("input", aplicarFiltros);
elementos.categoria.addEventListener("change", aplicarFiltros);
elementos.disponibles.addEventListener("change", aplicarFiltros);

elementos.formulario.addEventListener("reset", () => {
    // Espera a que el navegador restablezca los campos.
    setTimeout(aplicarFiltros, 0);
});

elementos.recargar.addEventListener("click", cargarJoyas);

cargarJoyas();