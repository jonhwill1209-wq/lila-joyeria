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
import java.util.HashMap;
import java.util.Map;

@Named
@SessionScoped
public class CarritoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Pedido carrito;

    // Cantidades propuestas en el formulario.
    private final Map<Integer, Integer> cantidades = new HashMap<>();

    @PostConstruct
    public void iniciar() {
        carrito = new Pedido();
    }

    public void agregar(Joya joya) {
        agregar(joya, 1);
    }

    public void agregar(Joya joya, int cantidad) {
        if (joya == null) {
            mostrarError("Selecciona un producto válido.");
            return;
        }

        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
            return;
        }

        DetallePedido existente = buscarDetalle(joya.getIdJoya());

        int cantidadActual = existente == null
                ? 0
                : existente.getCantidad();

        // Usamos long para evitar desbordamientos al sumar.
        long nuevaCantidad = (long) cantidadActual + cantidad;

        if (nuevaCantidad > joya.getStock()) {
            mostrarError("No hay suficiente inventario disponible.");
            return;
        }

        if (existente != null) {
            existente.setCantidad((int) nuevaCantidad);
        } else {
            DetallePedido detalle = new DetallePedido();
            detalle.setJoya(joya);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(joya.getPrecio());

            carrito.agregarDetalle(detalle);
        }

        cantidades.put(joya.getIdJoya(), (int) nuevaCantidad);
        carrito.calcularTotal();
        mostrarExito("Producto agregado al carrito.");
    }

    public void actualizarCantidad(DetallePedido detalle) {
        if (detalle == null || detalle.getJoya() == null) {
            mostrarError("Selecciona un producto válido.");
            return;
        }

        int idJoya = detalle.getJoya().getIdJoya();
        DetallePedido actual = buscarDetalle(idJoya);

        if (actual == null) {
            mostrarError("El producto ya no está en el carrito.");
            return;
        }

        Integer nuevaCantidad = cantidades.get(idJoya);

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
            return;
        }

        if (nuevaCantidad > actual.getJoya().getStock()) {
            mostrarError("La cantidad supera el inventario disponible.");
            return;
        }

        actual.setCantidad(nuevaCantidad);
        carrito.calcularTotal();

        mostrarExito("Cantidad actualizada correctamente.");
    }

    public void eliminar(DetallePedido detalle) {
        if (detalle != null) {
            cantidades.remove(detalle.getJoya().getIdJoya());
            carrito.eliminarDetalle(detalle);
            carrito.calcularTotal();

            mostrarExito("Producto eliminado del carrito.");
        }
    }

    public void vaciar() {
        carrito = new Pedido();
        cantidades.clear();
    }

    public void vaciarConMensaje() {
        vaciar();
        mostrarExito("Carrito vaciado correctamente.");
    }

    public Pedido getCarrito() {
        return carrito;
    }

    public BigDecimal getTotal() {
        return carrito.getTotal();
    }

    public Map<Integer, Integer> getCantidades() {
        for (DetallePedido detalle : carrito.getDetalles()) {
            cantidades.putIfAbsent(
                    detalle.getJoya().getIdJoya(),
                    detalle.getCantidad()
            );
        }

        return cantidades;
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

    private void mostrarExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Éxito",
                        mensaje
                )
        );
    }
}