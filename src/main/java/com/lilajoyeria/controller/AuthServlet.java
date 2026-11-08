package com.lilajoyeria.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.dto.LoginRequestDTO;
import com.lilajoyeria.dto.UsuarioResponseDTO;
import com.lilajoyeria.bean.AuthBean;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.ValidatorUtil;
import com.lilajoyeria.utilidades.ApiResponse;
import com.lilajoyeria.utilidades.ApiResponseWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet encargado de gestionar la autenticación de usuarios en la API REST.
 * Expone POST /api/auth/login (valida credenciales y abre una sesión HTTP) y
 * POST /api/auth/logout (cierra la sesión).
 * La sesión viaja en la cookie JSESSIONID; guarda el rol bajo AuthBean.ATRIBUTO_ROL,
 * el mismo atributo que leen AccesoFilter y ApiAdminFilter.
 */
@WebServlet({"/api/auth/login", "/api/auth/logout"})
public class AuthServlet extends HttpServlet {

    /** Atributo de sesión con los datos públicos del usuario autenticado por la API. */
    public static final String ATRIBUTO_USUARIO_API = "usuarioApi";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Procesa las peticiones HTTP POST para el inicio de sesión.
     * Lee las credenciales en formato JSON, valida campos obligatorios y formato de correo,
     * y retorna los datos del usuario omitiendo información sensible (como la contraseña).
     *
     * @param request  Objeto HttpServletRequest que contiene la solicitud HTTP con el JSON de credenciales.
     * @param response Objeto HttpServletResponse utilizado para enviar la respuesta HTTP en formato JSON.
     * @throws IOException Si ocurre un error de entrada/salida durante la lectura del cuerpo o escritura de la respuesta.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Configurar la respuesta como JSON con codificación UTF-8
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (request.getServletPath().endsWith("/logout")) {
            cerrarSesion(request, response);
            return;
        }

        try {
            // Deserializar el cuerpo de la solicitud JSON al DTO de inicio de sesión
            LoginRequestDTO loginDTO = MAPPER.readValue(request.getReader(), LoginRequestDTO.class);

            // Aplicar validaciones centralizadas (correo válido y contraseña obligatoria)
            ValidatorUtil.validarNoNulo(loginDTO, "cuerpo de la petición");
            ValidatorUtil.validarEmail(loginDTO.getEmail());
            ValidatorUtil.validarTextoObligatorio(loginDTO.getPassword(), "password");

            // Validar credenciales consultando la base de datos a través del DAO
            Usuario usuario = usuarioDAO.validarLogin(loginDTO.getEmail(), loginDTO.getPassword());

            if (usuario != null) {
                // Mapear al DTO de respuesta para proteger datos sensibles
                UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                        usuario.getIdUsuario(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getRol() != null ? usuario.getRol().name() : "CLIENTE"
                );

                // Crear la sesión (evitando fijación de sesión si ya existía una)
                abrirSesion(request, usuario, responseDTO);

                // Retornar 200 OK con la estructura ApiResponse exitosa
                ApiResponseWriter.write(response, HttpServletResponse.SC_OK,
                        new ApiResponse<>(true, "Login exitoso", responseDTO), LOGGER, "iniciar sesión");
            } else {
                // Retornar 401 Unauthorized si las credenciales son incorrectas
                ApiResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                        new ApiResponse<>(false, "Credenciales inválidas", null), LOGGER, "iniciar sesión");
            }

        } catch (JsonProcessingException e) {
            // JSON mal formado o con tipos incorrectos (400 Bad Request)
            ApiResponseWriter.write(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, "El cuerpo de la petición no es un JSON válido", null),
                    LOGGER, "iniciar sesión");
        } catch (IllegalArgumentException e) {
            // Capturar errores de validación de negocio (400 Bad Request)
            ApiResponseWriter.write(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage(), null), LOGGER, "iniciar sesión");
        } catch (Exception e) {
            // Capturar errores inesperados del servidor (500 Internal Server Error)
            ApiResponseWriter.writeInternalError(response, LOGGER, "iniciar sesión", e);
        }
    }

    /**
     * Crea la sesión HTTP del usuario autenticado. Si ya existía una sesión previa se
     * cambia su id (changeSessionId) para evitar fijación de sesión.
     */
    private void abrirSesion(HttpServletRequest request, Usuario usuario, UsuarioResponseDTO dto) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            sesion = request.getSession(true);
        } else {
            request.changeSessionId();
        }
        sesion.setAttribute(AuthBean.ATRIBUTO_ROL,
                usuario.getRol() != null ? usuario.getRol() : RolUsuario.CLIENTE);
        sesion.setAttribute(ATRIBUTO_USUARIO_API, dto);
    }

    /** Invalida la sesión actual (si la hay) y responde 200 OK. */
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        ApiResponseWriter.write(response, HttpServletResponse.SC_OK,
                new ApiResponse<>(true, "Sesión cerrada", null), LOGGER, "cerrar sesión");
    }
}