package com.lilajoyeria.controller;
import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.Joya;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// El enrutador que controla la vista del catálogo
@WebServlet(name = "CatalogoServlet", urlPatterns = {"/catalogo"})
public class CatalogoServlet extends HttpServlet {

    private JoyaDAO joyaDAO;

    @Override
    public void init() {
        // Inicializamos el DAO que consultará la base de datos
        joyaDAO = new JoyaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            //Obtener los datos usando el DAO
            List<Joya> listaJoyas = joyaDAO.listar();

            //Guardar los datos en el request para que la vista
            request.setAttribute("joyas", listaJoyas);

            //Redirigir de manera controlada hacia la vista construida con JSP
            request.getRequestDispatcher("/catalogo.jsp").forward(request, response);

        } catch (Exception e) {
            // Manejo de errores y retroalimentación
            request.setAttribute("error", "Error al cargar el catálogo de joyas.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}