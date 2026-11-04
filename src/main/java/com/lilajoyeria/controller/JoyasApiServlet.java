package com.lilajoyeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.utilidades.ApiResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/joyas/*")
public class JoyasApiServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JoyaDAO joyaDAO = new JoyaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        configurarRespuesta(response);
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            listarJoyas(response);
            return;
        }

        String idPath = pathInfo.substring(1);
        if (idPath.isEmpty() || idPath.contains("/")) {
            escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, "El identificador de la joya no es válido", null));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idPath);
        } catch (NumberFormatException e) {
            escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, "El identificador de la joya debe ser un número entero", null));
            return;
        }

        if (id <= 0) {
            escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, "El identificador de la joya debe ser mayor que cero", null));
            return;
        }

        buscarJoya(response, id);
    }

    private void listarJoyas(HttpServletResponse response) throws IOException {
        try {
            List<Joya> joyas = joyaDAO.listar();
            escribirRespuesta(response, HttpServletResponse.SC_OK,
                    new ApiResponse<>(true, "Catálogo de joyas", joyas));
        } catch (SQLException e) {
            escribirErrorInterno(response);
        }
    }

    private void buscarJoya(HttpServletResponse response, int id) throws IOException {
        try {
            Joya joya = joyaDAO.buscarPorId(id);
            if (joya == null) {
                escribirRespuesta(response, HttpServletResponse.SC_NOT_FOUND,
                        new ApiResponse<>(false, "No se encontró la joya solicitada", null));
                return;
            }
            escribirRespuesta(response, HttpServletResponse.SC_OK,
                    new ApiResponse<>(true, "Joya encontrada", joya));
        } catch (SQLException e) {
            escribirErrorInterno(response);
        }
    }

    private void configurarRespuesta(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void escribirErrorInterno(HttpServletResponse response) throws IOException {
        escribirRespuesta(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new ApiResponse<>(false, "Ocurrió un error al consultar el catálogo", null));
    }

    private void escribirRespuesta(HttpServletResponse response, int status, ApiResponse<?> cuerpo)
            throws IOException {
        response.setStatus(status);
        MAPPER.writeValue(response.getOutputStream(), cuerpo);
    }
}
