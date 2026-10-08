package com.lilajoyeria.bean;

import com.lilajoyeria.model.DetallePedido;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.model.Pedido;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;

@Named
@SessionScoped
public class CarritoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Pedido carrito;

    @PostConstruct
    public void iniciar() {
        carrito = new Pedido();
    }

    public void agregar(Joya joya) {
        agregar(joya, 1);
    }

    public void agregar(Joya joya, int cantidad) {
        if (joya == null || cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
            return;
        }

        DetallePedido detalleExistente = buscarDetalle(joya.getIdJoya());
        int cantidadActual = detalleExistente == null
                ? 0
                : detalleExistente.getCantidad();

        if (cantidadActual + cantidad > joya.getStock()) {
            mostrarError("No hay suficiente inventario disponible.");
            return;
        }

        if (detalleExistente != null) {
            detalleExistente.setCantidad(cantidadActual + cantidad);
        } else {
            DetallePedido detalle = new DetallePedido();
            detalle.setJoya(joya);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(joya.getPrecio());

            carrito.agregarDetalle(detalle);
        }

        carrito.calcularTotal();

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Éxito",
                        "Producto agregado al carrito."
                )
        );
    }

    public void eliminar(DetallePedido detalle) {
        if (detalle != null) {
            carrito.eliminarDetalle(detalle);
            carrito.calcularTotal();
        }
    }

    public void vaciar() {
        carrito = new Pedido();
    }

    public Pedido getCarrito() {
        return carrito;
    }

    public BigDecimal getTotal() {
        return carrito.getTotal();
    }

    private DetallePedido buscarDetalle(int idJoya) {
        for (DetallePedido detalle : carrito.getDetalles()) {
            if (detalle.getJoya().getIdJoya() == idJoya) {
                return detalle;
            }
        }
        return null;
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