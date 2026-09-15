package com.lilajoyeria.controller;

import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.dao.PedidoDAO;
import com.lilajoyeria.model.DetallePedido;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.model.Pedido;
import com.lilajoyeria.model.Usuario;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    private JoyaDAO joyaDAO;
    private PedidoDAO pedidoDAO;

    @Override
    public void init() {
        joyaDAO = new JoyaDAO();
        pedidoDAO = new PedidoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/carrito.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if ("realizarPedido".equals(request.getParameter("accion"))) {
            realizarPedido(request, response);
            return;
        }

        String idJoyaParam = request.getParameter("idJoya");
        String cantidadParam = request.getParameter("cantidad");

        if (idJoyaParam == null ||
                idJoyaParam.trim().isEmpty() ||
                cantidadParam == null ||
                cantidadParam.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/catalogo?error=DatosInvalidos"
            );
            return;
        }

        try {

            int idJoya = Integer.parseInt(idJoyaParam);
            int cantidad = Integer.parseInt(cantidadParam);

            if (cantidad <= 0) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/catalogo?error=DatosInvalidos"
                );
                return;
            }

            Joya joya = joyaDAO.buscarPorId(idJoya);

            if (joya == null) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/catalogo?error=JoyaNoEncontrada"
                );
                return;
            }

            HttpSession session = request.getSession();

            Pedido carrito =
                    (Pedido) session.getAttribute("carrito");

            if (carrito == null) {

                carrito = new Pedido();

                Usuario usuario =
                        (Usuario) session.getAttribute(
                                "usuarioLogueado"
                        );

                if (usuario != null) {
                    carrito.setUsuario(usuario);
                }
            }

            DetallePedido detalle =
                    new DetallePedido();

            detalle.setJoya(joya);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(
                    joya.getPrecio()
            );

            carrito.agregarDetalle(detalle);

            session.setAttribute(
                    "carrito",
                    carrito
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/carrito"
            );

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/catalogo?error=DatosInvalidos"
            );

        } catch (Exception e) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/catalogo?error=ErrorAlAgregar"
            );
        }
    }

    private void realizarPedido(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogueado");
        Pedido carrito =
                (Pedido) session.getAttribute("carrito");

        if (usuario == null) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/login?continuar=carrito"
            );
            return;
        }

        if (carrito == null || carrito.getDetalles().isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + "/carrito"
            );
            return;
        }

        try {
            carrito.setUsuario(usuario);
            pedidoDAO.guardar(carrito);
            session.removeAttribute("carrito");

            response.sendRedirect(
                    request.getContextPath()
                            + "/carrito?pedido=creado"
            );
        } catch (SQLException | RuntimeException exception) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/carrito?error=guardar"
            );
        }
    }
}