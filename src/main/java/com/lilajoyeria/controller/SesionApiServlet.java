package com.lilajoyeria.controller;

import com.lilajoyeria.dto.UsuarioResponseDTO;
import com.lilajoyeria.utilidades.ApiResponse;
import com.lilajoyeria.utilidades.ApiResponseWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/api/auth/sesion")
public class SesionApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SesionApiServlet.class.getName());

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession sesion = request.getSession(false);

        Object usuario = sesion == null
                ? null
                : sesion.getAttribute(AuthServlet.ATRIBUTO_USUARIO_API);

        if (!(usuario instanceof UsuarioResponseDTO)) {
            ApiResponseWriter.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    new ApiResponse<>(false, "No hay una sesión iniciada en la API", null),
                    LOGGER,
                    "consultar sesión"
            );
            return;
        }

        ApiResponseWriter.write(
                response,
                HttpServletResponse.SC_OK,
                new ApiResponse<>(true, "Sesión activa", usuario),
                LOGGER,
                "consultar sesión"
        );
    }
}