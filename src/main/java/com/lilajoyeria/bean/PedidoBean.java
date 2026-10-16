package com.lilajoyeria.bean;

import com.lilajoyeria.dao.PedidoDAO;
import com.lilajoyeria.model.Pedido;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class PedidoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(PedidoBean.class.getName());

    @Inject
    private AuthBean authBean;

    @Inject
    private CarritoBean carritoBean;

    private transient PedidoDAO pedidoDAO;

    @PostConstruct
    public void iniciar() {
        pedidoDAO = new PedidoDAO();
    }

    private PedidoDAO obtenerPedidoDAO() {
        if (pedidoDAO == null) {
            pedidoDAO = new PedidoDAO();
        }

        return pedidoDAO;
    }

    public String confirmarPedido() {
        if (!authBean.isAutenticado()) {
            mostrarError("Debes iniciar sesión para realizar un pedido.");
            return null;
        }

        Pedido carrito = carritoBean.getCarrito();

        if (carrito == null || carrito.getDetalles().isEmpty()) {
            mostrarError("El carrito está vacío.");
            return null;
        }

        try {
            carrito.setUsuario(authBean.getUsuarioActual());
            carrito.calcularTotal();

            obtenerPedidoDAO().guardar(carrito);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar el pedido.", e);

            String mensaje = e.getMessage();

            if (mensaje == null
                    || !mensaje.startsWith("No hay inventario suficiente")) {
                mensaje = "No fue posible guardar el pedido.";
            }

            mostrarError(mensaje);
            return null;
        }

        // Vaciar únicamente después de guardar correctamente.
        carritoBean.vaciar();

        FacesContext contexto = FacesContext.getCurrentInstance();

        contexto.getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        contexto.addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Éxito",
                        "Pedido registrado correctamente. "
                                + "Puedes consultarlo en Mis pedidos."
                )
        );

        return "/pedidos.xhtml?faces-redirect=true";
    }

    private void mostrarError(String mensaje) {
        FacesContext contexto = FacesContext.getCurrentInstance();

        contexto.validationFailed();

        contexto.addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error",
                        mensaje
                )
        );
    }
}