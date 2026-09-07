package com.lilajoyeria.controller;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//El enrutador que controla los formularios de registro de la tienda
@WebServlet(name = "RegistroServlet", urlPatterns = {"/registro"})
public class RegistroServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        //Inicializamos el DAO para la conexión con los datos
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Redirecciona a la vista del formulario si se accede por URL directamente
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Valida parámetros recibidos por POST desde el formulario
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        //Prevención de operaciones inválidas y validaciones en el servidor
        if (nombre == null || nombre.trim().isEmpty() ||
                correo == null || correo.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {

            //Envia mensaje de error claro a la vista si faltan datos
            request.setAttribute("error", "Todos los campos son obligatorios para el registro.");
            //Reenvia la respuesta de manera controlada
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        try {
            //Coordinar la comunicación entre la vista y los datos
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(correo);
            nuevoUsuario.setPassword(password);

            int idGenerado = usuarioDAO.insertar(nuevoUsuario);

            //Reenviamos las respuestas de manera controlada
            if (idGenerado > 0) {
                // Mensaje de éxito claro para el cliente
                request.setAttribute("mensajeExito", "Registro completado con éxito. Ahora puede iniciar sesión.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "No se pudo registrar el usuario. Intente nuevamente.");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
            }

        } catch (Exception e) {
            //Control integral de peticiones y errores
            request.setAttribute("error", "Ocurrió un error inesperado o el correo ya está en uso.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }
}