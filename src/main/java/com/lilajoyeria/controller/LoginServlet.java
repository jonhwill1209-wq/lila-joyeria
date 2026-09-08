package com.lilajoyeria.controller;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//El enrutador que controla el inicio de sesión
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Redirije a la vista si acceden por GET
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Valida parámetros recibidos por POST
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar correo y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            //Coordina la comunicación con los datos
            Usuario usuario = usuarioDAO.validarLogin(correo, password);

            //Reenvia las respuestas de manera controlada
            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogueado", usuario);
                // Redirección exitosa hacia el catálogo
                response.sendRedirect(request.getContextPath() + "/catalogo");
            } else {
                //Retroalimentación de error hacia la vista
                request.setAttribute("error", "Credenciales incorrectas.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            //Capturamos la SQLException y cualquier otro error
            request.setAttribute("error", "Ocurrió un error de conexión al intentar iniciar sesión.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}