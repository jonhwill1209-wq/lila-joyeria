package com.lilajoyeria.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilajoyeria.bean.AuthBean;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.utilidades.ApiResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Control de acceso de la API REST: las lecturas (GET) de /api/joyas/* son públicas,
 * pero POST, PUT y DELETE exigen una sesión iniciada con rol ADMIN.
 * <ul>
 *   <li>Sin sesión → 401 Unauthorized</li>
 *   <li>Sesión con rol distinto de ADMIN → 403 Forbidden</li>
 * </ul>
 * Usa el mismo atributo de sesión que AccesoFilter (AuthBean.ATRIBUTO_ROL), así que
 * el login de la API y el de JSF comparten el mismo control de rol.
 */
@WebFilter(filterName = "ApiAdminFilter", urlPatterns = {"/api/joyas", "/api/joyas/*"})
public class ApiAdminFilter implements Filter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String metodo = req.getMethod();
        boolean esLectura = "GET".equalsIgnoreCase(metodo)
                || "HEAD".equalsIgnoreCase(metodo)
                || "OPTIONS".equalsIgnoreCase(metodo);

        if (esLectura) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(false);
        Object rol = sesion == null ? null : sesion.getAttribute(AuthBean.ATRIBUTO_ROL);

        if (rol == null) {
            rechazar(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Debes iniciar sesión para realizar esta operación");
            return;
        }

        if (rol != RolUsuario.ADMIN) {
            rechazar(res, HttpServletResponse.SC_FORBIDDEN,
                    "Solo un administrador puede realizar esta operación");
            return;
        }

        chain.doFilter(request, response);
    }

    private void rechazar(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(res.getOutputStream(), new ApiResponse<>(false, mensaje, null));
    }
}
