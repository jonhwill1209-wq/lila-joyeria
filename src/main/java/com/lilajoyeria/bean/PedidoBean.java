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
    private String nombreContacto;
    private String direccionEntrega;
    private String telefonoWhatsapp;

    @PostConstruct
    public void iniciar() {
        pedidoDAO = new PedidoDAO();
        if (authBean.getUsuarioActual() != null) {
            nombreContacto = authBean.getUsuarioActual().getNombre();
        }
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

        String errorEntrega = validarDatosEntrega();
        if (errorEntrega != null) {
            mostrarError(errorEntrega);
            return null;
        }

        try {
            carrito.setUsuario(authBean.getUsuarioActual());
            carrito.setNombreContacto(nombreContacto);
            carrito.setDireccionEntrega(direccionEntrega);
            carrito.setTelefonoWhatsapp(telefonoWhatsapp);
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

    private String validarDatosEntrega() {
        if (nombreContacto == null || nombreContacto.trim().isEmpty()
                || direccionEntrega == null
                || direccionEntrega.trim().isEmpty()
                || telefonoWhatsapp == null
                || telefonoWhatsapp.trim().isEmpty()) {
            return "Completa el nombre, la dirección y el teléfono de WhatsApp.";
        }
        if (nombreContacto.trim().length() > 100) {
            return "El nombre no puede superar 100 caracteres.";
        }
        if (direccionEntrega.trim().length() > 255) {
            return "La dirección no puede superar 255 caracteres.";
        }
        String numeros = telefonoWhatsapp.replaceAll("\\D", "");
        if (telefonoWhatsapp.trim().length() > 30
                || !telefonoWhatsapp.trim().matches("[+0-9() .-]{7,30}")
                || numeros.length() < 7 || numeros.length() > 15) {
            return "Ingresa un teléfono de WhatsApp válido (de 7 a 15 dígitos).";
        }
        return null;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTelefonoWhatsapp() {
        return telefonoWhatsapp;
    }

    public void setTelefonoWhatsapp(String telefonoWhatsapp) {
        this.telefonoWhatsapp = telefonoWhatsapp;
    }
}