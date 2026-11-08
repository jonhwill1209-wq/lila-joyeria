"use strict";

(() => {
    const API_BASE_URL = new URL(
        document.querySelector('meta[name="api-base-url"]').content,
        document.baseURI
    );
    const API_LOGIN = new URL("auth/login", API_BASE_URL);

    const formulario = document.getElementById("formLogin");
    const correo = document.getElementById("email");
    const password = document.getElementById("password");
    const boton = document.getElementById("btnLogin");
    const mensaje = document.getElementById("mensajeLogin");

    let enviando = false;

    function mostrarMensaje(texto, tipo = "error") {
        mensaje.textContent = texto;
        mensaje.className = `mensaje ${tipo}`;
        mensaje.hidden = false;
    }

    function establecerCarga(activa) {
        enviando = activa;

        boton.disabled = activa;
        correo.readOnly = activa;
        password.readOnly = activa;

        boton.textContent = activa
            ? "Iniciando sesión…"
            : "Iniciar sesión";

        formulario.setAttribute("aria-busy", String(activa));
    }

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        if (enviando) {
            return;
        }

        correo.value = correo.value.trim();

        if (!formulario.reportValidity()) {
            return;
        }

        mensaje.hidden = true;
        mensaje.textContent = "";

        // La contraseña se envía tal como fue escrita.
        // No se guarda en localStorage ni en sessionStorage.
        const credenciales = {
            email: correo.value,
            password: password.value
        };

        const controlador = new AbortController();
        let tiempoAgotado = false;
        let accesoCorrecto = false;

        const tiempoLimite = setTimeout(() => {
            tiempoAgotado = true;
            controlador.abort();
        }, 15000);

        establecerCarga(true);

        try {
            const respuesta = await fetch(API_LOGIN, {
                method: "POST",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json"
                },

                // Envía y recibe la cookie de sesión del mismo servidor.
                credentials: "include",

                cache: "no-store",
                signal: controlador.signal,
                body: JSON.stringify(credenciales)
            });

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
                const textoServidor =
                    typeof cuerpo?.mensaje === "string"
                        ? cuerpo.mensaje.trim()
                        : "";

                throw new Error(
                    textoServidor ||
                    `No se pudo iniciar sesión (HTTP ${respuesta.status}).`
                );
            }

            accesoCorrecto = true;
            password.value = "";

            mostrarMensaje(
                "Sesión iniciada. Abriendo el catálogo…",
                "exito"
            );

            // La sesión ya quedó establecida en el servidor.
            window.location.replace(
                new URL("index.html", document.baseURI).href
            );
        } catch (error) {
            password.value = "";

            const texto = tiempoAgotado
                ? "La solicitud tardó demasiado. Intenta nuevamente."
                : error instanceof TypeError
                    ? "No se pudo conectar con el servidor. Comprueba que Tomcat esté iniciado."
                    : error.message || "Ocurrió un error al iniciar sesión.";

            mostrarMensaje(texto);
        } finally {
            clearTimeout(tiempoLimite);

            if (!accesoCorrecto) {
                establecerCarga(false);
                password.focus();
            }
        }
    });
})();