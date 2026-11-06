package com.lilajoyeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.dto.JoyaDTO;
import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.util.ValidatorUtil;
import com.lilajoyeria.utilidades.ApiResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet central para la gestión de operaciones REST sobre el recurso Joyas (/api/joyas/*).
 * Maneja tanto la lectura pública (GET) como las operaciones mutadoras
 * de creación (POST), actualización (PUT) y eliminación (DELETE).
 */
@WebServlet("/api/joyas/*")
public class JoyasApiServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Instancia del DAO inicializada mediante el constructor por defecto JPAUtil
    private final JoyaDAO joyaDAO = new JoyaDAO();

    /**
     * Maneja las peticiones HTTP GET públicas para listar todas las joyas o buscar una por su ID.
     *
     * @param request  Petición HTTP entrante.
     * @param response Respuesta HTTP saliente en formato JSON.
     * @throws IOException Si ocurre un error de E/S.
     */
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

    /**
     * Procesa peticiones HTTP POST para registrar una nueva joya en el sistema.
     * Valida reglas de negocio (nombre obligatorio, precio mayor a cero, stock no nulo)
     * y retorna un código 201 Created si la operación es exitosa.
     *
     * @param request  Petición HTTP con el JSON de la nueva joya.
     * @param response Respuesta HTTP con el recurso creado.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarRespuesta(response);

        try {
            // Deserializar el JSON recibido en el DTO de Joya
            JoyaDTO joyaDTO = MAPPER.readValue(request.getReader(), JoyaDTO.class);

            // Validar reglas de negocio centralizadas
            ValidatorUtil.validarTextoObligatorio(joyaDTO.getNombre(), "nombre");
            ValidatorUtil.validarPrecioMayorQueCero(joyaDTO.getPrecio());
            ValidatorUtil.validarNoNulo(joyaDTO.getStock(), "stock");

            // Mapear DTO al modelo de dominio
            Joya nuevaJoya = new Joya();
            nuevaJoya.setNombre(joyaDTO.getNombre());
            nuevaJoya.setPrecio(joyaDTO.getPrecio());
            nuevaJoya.setStock(joyaDTO.getStock());

            // Asignar el objeto Categoria requerido por el modelo Joya
            if (joyaDTO.getIdCategoria() > 0) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(joyaDTO.getIdCategoria());
                nuevaJoya.setCategoria(categoria);
            }

            // Persistir mediante el DAO utilizando el método correcto 'insertar' (retorna int ID)
            int idGenerado = joyaDAO.insertar(nuevaJoya);
            if (idGenerado > 0) {
                nuevaJoya.setIdJoya(idGenerado);
                escribirRespuesta(response, HttpServletResponse.SC_CREATED,
                        new ApiResponse<>(true, "Joya creada exitosamente", nuevaJoya));
            } else {
                escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiResponse<>(false, "No se pudo registrar la joya", null));
            }
        } catch (IllegalArgumentException e) {
            // Manejo de errores de validación de entradas (400 Bad Request)
            escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            // Manejo de excepciones generales / base de datos (500 Internal Server Error)
            escribirErrorInterno(response);
        }
    }

    /**
     * Procesa peticiones HTTP PUT para actualizar una joya existente identificada por su ID en la ruta.
     * Valida que el recurso exista y cumpla con las restricciones de validación.
     *
     * @param request  Petición HTTP que incluye el ID en la URL y el JSON con los nuevos datos.
     * @param response Respuesta HTTP con el estado de la actualización.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarRespuesta(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiResponse<>(false, "ID de joya requerido para actualizar", null));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            JoyaDTO joyaDTO = MAPPER.readValue(request.getReader(), JoyaDTO.class);

            // Validaciones de entrada
            ValidatorUtil.validarTextoObligatorio(joyaDTO.getNombre(), "nombre");
            ValidatorUtil.validarPrecioMayorQueCero(joyaDTO.getPrecio());

            // Verificar existencia previa de la joya
            Joya joyaExistente = joyaDAO.buscarPorId(id);
            if (joyaExistente == null) {
                escribirRespuesta(response, HttpServletResponse.SC_NOT_FOUND,
                        new ApiResponse<>(false, "Joya no encontrada", null));
                return;
            }

            // Actualizar campos del modelo
            joyaExistente.setNombre(joyaDTO.getNombre());
            joyaExistente.setPrecio(joyaDTO.getPrecio());
            joyaExistente.setStock(joyaDTO.getStock());

            if (joyaDTO.getIdCategoria() > 0) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(joyaDTO.getIdCategoria());
                joyaExistente.setCategoria(categoria);
            }

            boolean actualizado = joyaDAO.actualizar(joyaExistente);
            if (actualizado) {
                escribirRespuesta(response, HttpServletResponse.SC_OK,
                        new ApiResponse<>(true, "Joya actualizada exitosamente", joyaExistente));
            } else {
                escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiResponse<>(false, "No se pudo actualizar la joya", null));
            }
        } catch (IllegalArgumentException e) {
            escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            escribirErrorInterno(response);
        }
    }

    /**
     * Procesa peticiones HTTP DELETE para eliminar una joya específica según su ID en la URL.
     * Retorna un código 204 No Content si se elimina correctamente o 404 Not Found si no existe.
     *
     * @param request  Petición HTTP con el ID de la joya en la ruta.
     * @param response Respuesta HTTP con el resultado de la eliminación.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarRespuesta(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                escribirRespuesta(response, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiResponse<>(false, "ID de joya requerido para eliminar", null));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean eliminado = joyaDAO.eliminar(id);

            if (eliminado) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content exitoso
            } else {
                escribirRespuesta(response, HttpServletResponse.SC_NOT_FOUND,
                        new ApiResponse<>(false, "Joya no encontrada", null));
            }
        } catch (Exception e) {
            escribirErrorInterno(response);
        }
    }

    /**
     * Metodo auxiliar para listar todas las joyas registradas.
     */
    private void listarJoyas(HttpServletResponse response) throws IOException {
        try {
            List<Joya> joyas = joyaDAO.listar();
            escribirRespuesta(response, HttpServletResponse.SC_OK,
                    new ApiResponse<>(true, "Catálogo de joyas", joyas));
        } catch (SQLException e) {
            escribirErrorInterno(response);
        }
    }

    /**
     * Metodo auxiliar para buscar una joya por su identificador único.
     */
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

    /**
     * Configura los encabezados estándar de la respuesta HTTP para asegurar formato JSON y codificación UTF-8.
     */
    private void configurarRespuesta(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    /**
     * Envía una respuesta estandarizada ante errores internos de servidor (500 Internal Server Error).
     */
    private void escribirErrorInterno(HttpServletResponse response) throws IOException {
        escribirRespuesta(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new ApiResponse<>(false, "Ocurrió un error en el servidor", null));
    }

    /**
     * Serializa y escribe el objeto ApiResponse estructurado en el flujo de salida de la respuesta.
     */
    private void escribirRespuesta(HttpServletResponse response, int status, ApiResponse<?> cuerpo)
            throws IOException {
        response.setStatus(status);
        MAPPER.writeValue(response.getOutputStream(), cuerpo);
    }
}