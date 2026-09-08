package com.lilajoyeria.controller;

import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.DetallePedido;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.model.Pedido;
import com.lilajoyeria.model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//El enrutador que controla el carrito de compras
@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    private JoyaDAO joyaDAO;

    @Override
    public void init() {
        //Inicializa el DAO para consultar la base de datos
        joyaDAO = new JoyaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Valida parámetros recibidos por POST
        String idJoyaParam = request.getParameter("idJoya");
        String cantidadParam = request.getParameter("cantidad");

        if (idJoyaParam == null || idJoyaParam.trim().isEmpty() || cantidadParam == null) {
            //Redirigir si la operación es inválida o faltan datos
            response.sendRedirect(request.getContextPath() + "/catalogo?error=DatosInvalidos");
            return;
        }

        try {
            int idJoya = Integer.parseInt(idJoyaParam);
            int cantidad = Integer.parseInt(cantidadParam);

            //Busca la joya en la base de datos usando el DAO
            Joya joya = joyaDAO.buscarPorId(idJoya);

            if (joya != null) {
                HttpSession session = request.getSession();

                //Recupera el carrito actual de la sesión o crea uno nuevo
                Pedido carrito = (Pedido) session.getAttribute("carrito");
                if (carrito == null) {
                    carrito = new Pedido();

                    //Asigna el usuario al pedido si ya inició sesión
                    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
                    if (usuario != null) {
                        carrito.setUsuario(usuario);
                    }
                }

                //Crea el nuevo detalle del pedido
                DetallePedido detalle = new DetallePedido();
                detalle.setJoya(joya);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(joya.getPrecio());

                //Agrega el detalle al pedido y recalcula el total
                carrito.agregarDetalle(detalle);

                //Guarda el carrito actualizado de vuelta en la sesión
                session.setAttribute("carrito", carrito);
            }

            //Enviar los datos correctos a la vista del carrito
            response.sendRedirect(request.getContextPath() + "/carrito.jsp");

        } catch (Exception e) {
            //Prevenir operaciones inválidas o errores de procesamiento
            response.sendRedirect(request.getContextPath() + "/catalogo?error=ErrorAlAgregar");
        }
    }
}