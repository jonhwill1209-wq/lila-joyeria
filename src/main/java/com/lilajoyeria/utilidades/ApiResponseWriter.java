package com.lilajoyeria.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApiResponseWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiResponseWriter() {
    }

    public static void write(
            HttpServletResponse response,
            int status,
            ApiResponse<?> body,
            Logger logger,
            String operation
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if (!body.isExito() && logger != null) {
            logger.log(
                    status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                            ? Level.SEVERE
                            : Level.WARNING,
                    "API {0}: HTTP {1} - {2}",
                    new Object[]{operation, status, body.getMensaje()}
            );
        }

        MAPPER.writeValue(response.getOutputStream(), body);
    }

    public static void writeInternalError(
            HttpServletResponse response,
            Logger logger,
            String operation,
            Exception exception
    ) throws IOException {
        logger.log(Level.SEVERE, "Error interno en API: " + operation, exception);
        write(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new ApiResponse<>(false, "Ocurrió un error en el servidor", null),
                null,
                operation
        );
    }
}
