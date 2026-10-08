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

@Named
@ViewScoped
public class PedidoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AuthBean authBean;

    @Inject
    private CarritoBean carritoBean;

    private transient PedidoDAO pedidoDAO;

    @PostConstruct
    public void iniciar() {
        pedidoDAO = new PedidoDAO();
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
            pedidoDAO.guardar(carrito);

            carritoBean.vaciar();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Pedido realizado correctamente."
                    )
            );

            return "catalogo?faces-redirect=true";

        }  catch (Exception e) {
        String mensaje = e.getMessage();

        if (mensaje == null ||
                !mensaje.startsWith(
                        "No hay inventario suficiente"
                )) {

            mensaje = "No fue posible guardar el pedido.";
        }

        mostrarError(mensaje);
        return null;
    }

    }

    private void mostrarError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error",
                        mensaje
                )
        );
    }
}