package com.lilajoyeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.dto.UsuarioResponseDTO;
import com.lilajoyeria.utilidades.ApiResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/sesion")
public class SesionApiServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession sesion = request.getSession(false);

        Object usuario = sesion == null
                ? null
                : sesion.getAttribute(AuthServlet.ATRIBUTO_USUARIO_API);

        if (!(usuario instanceof UsuarioResponseDTO)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            MAPPER.writeValue(
                    response.getOutputStream(),
                    new ApiResponse<>(
                            false,
                            "No hay una sesión iniciada en la API",
                            null
                    )
            );
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);

        MAPPER.writeValue(
                response.getOutputStream(),
                new ApiResponse<>(
                        true,
                        "Sesión activa",
                        usuario
                )
        );
    }
}