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

    // La API devuelve nombres como "anillo1.jpeg".
    imagen.src = new URL(
        `../img/${encodeURIComponent(archivo)}`,
        document.baseURI
    ).href;

    contenedor.append(imagen);
    return contenedor;
}

function crearTarjeta(joya) {
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
    const tiempoLimite = setTimeout(() => controlador.abort(), 15000);

    try {
        const respuesta = await fetch(API_JOYAS, {
            method: "GET",
            headers: {
                Accept: "application/json"
            },
            cache: "no-store",
            signal: controlador.signal
        });

        let cuerpo;

        try {
            cuerpo = await respuesta.json();
        } catch (error) {
            if (controlador.signal.aborted) {
                throw error;
            }

            throw new Error(
                `El servidor no devolvió JSON válido (HTTP ${
                    respuesta.status
                }).`
            );
        }

        if (!respuesta.ok || cuerpo?.exito !== true) {
            throw new Error(
                cuerpo?.mensaje ||
                `No se pudo consultar el catálogo (HTTP ${
                    respuesta.status
                }).`
            );
        }

        if (!Array.isArray(cuerpo.datos)) {
            throw new Error(
                "La respuesta del catálogo no contiene una lista de joyas."
            );
        }

        joyas = cuerpo.datos;

        if (joyas.some((joya) =>
            !joya || typeof joya !== "object" || Array.isArray(joya)
        )) {
            throw new Error("El catálogo contiene productos inválidos.");
        }

        cargarCategorias();
        cargaCorrecta = true;
    } catch (error) {
        joyas = [];

        const mensaje = controlador.signal.aborted
            ? "La consulta tardó demasiado. Intenta actualizar el catálogo."
            : error instanceof TypeError
                ? "No se pudo conectar con el servidor. Comprueba que Tomcat esté iniciado."
                : error.message || "Ocurrió un error al cargar el catálogo.";

        mostrarMensaje(mensaje);
    } finally {
        clearTimeout(tiempoLimite);
        establecerCarga(false);

        if (cargaCorrecta) {
            aplicarFiltros();
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