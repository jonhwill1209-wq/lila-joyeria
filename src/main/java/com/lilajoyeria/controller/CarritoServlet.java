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

@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    private JoyaDAO joyaDAO;

    @Override
    public void init() {
        joyaDAO = new JoyaDAO();
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
}