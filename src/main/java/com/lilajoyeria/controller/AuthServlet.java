package com.lilajoyeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.dto.LoginRequestDTO;
import com.lilajoyeria.dto.UsuarioResponseDTO;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.ValidatorUtil;
import com.lilajoyeria.utilidades.ApiResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado de gestionar la autenticación de usuarios en la API REST.
 * Expone el endpoint POST /api/auth/login para validar credenciales de forma segura.
 */
@WebServlet("/api/auth/login")
public class AuthServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();
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

        try {
            // Deserializar el cuerpo de la solicitud JSON al DTO de inicio de sesión
            LoginRequestDTO loginDTO = MAPPER.readValue(request.getReader(), LoginRequestDTO.class);

            // Aplicar validaciones centralizadas (correo válido y contraseña obligatoria)
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

                // Retornar 200 OK con la estructura ApiResponse exitosa
                response.setStatus(HttpServletResponse.SC_OK);
                MAPPER.writeValue(response.getOutputStream(), new ApiResponse<>(true, "Login exitoso", responseDTO));
            } else {
                // Retornar 401 Unauthorized si las credenciales son incorrectas
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                MAPPER.writeValue(response.getOutputStream(), new ApiResponse<>(false, "Credenciales inválidas", null));
            }

        } catch (IllegalArgumentException e) {
            // Capturar errores de validación de negocio (400 Bad Request)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            MAPPER.writeValue(response.getOutputStream(), new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            // Capturar errores inesperados del servidor (500 Internal Server Error)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            MAPPER.writeValue(response.getOutputStream(), new ApiResponse<>(false, "Error interno en el servidor", null));
        }
    }
}