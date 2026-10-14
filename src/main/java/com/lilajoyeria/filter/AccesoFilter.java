package com.lilajoyeria.filter;

import com.lilajoyeria.bean.AuthBean;
import com.lilajoyeria.model.RolUsuario;
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
 * Control de acceso por rol (RolUsuario):
 * <ul>
 *   <li>/admin/*       solo ADMIN</li>
 *   <li>/pedidos.xhtml cualquier usuario autenticado (ADMIN o CLIENTE)</li>
 * </ul>
 * El rol lo publica AuthBean en la sesión al iniciar sesión.
 */
@WebFilter(
        filterName = "AccesoFilter",
        urlPatterns = {"/admin/*", "/pedidos.xhtml"}
)
public class AccesoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Evita que el botón "atrás" muestre páginas privadas tras cerrar sesión.
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession sesion = req.getSession(false);
        RolUsuario rol = sesion == null
                ? null
                : (RolUsuario) sesion.getAttribute(AuthBean.ATRIBUTO_ROL);

        String raiz = req.getContextPath();
        String ruta = req.getRequestURI().substring(raiz.length());

        if (rol == null) {
            res.sendRedirect(raiz + "/login.xhtml");
            return;
        }

        if (ruta.startsWith("/admin/") && rol != RolUsuario.ADMIN) {
            res.sendRedirect(raiz + "/acceso-denegado.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}
