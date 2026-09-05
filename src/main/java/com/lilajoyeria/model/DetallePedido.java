package com.lilajoyeria.model;

import java.math.BigDecimal;

public class DetallePedido {

    private int idDetalle;
    private Pedido pedido;
    private Joya joya;
    private int cantidad;
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    public DetallePedido() {
    }

    public DetallePedido(Pedido pedido, Joya joya, int cantidad,
                         BigDecimal precioUnitario) {
        setPedido(pedido);
        setJoya(joya);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
    }

    public DetallePedido(int idDetalle, Pedido pedido, Joya joya,
                         int cantidad, BigDecimal precioUnitario) {
        setIdDetalle(idDetalle);
        setPedido(pedido);
        setJoya(joya);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        if (idDetalle < 0) {
            throw new IllegalArgumentException(
                    "El identificador del detalle no puede ser negativo"
            );
        }
        this.idDetalle = idDetalle;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido es obligatorio"
            );
        }
        this.pedido = pedido;
    }

    public Joya getJoya() {
        return joya;
    }

    public void setJoya(Joya joya) {
        if (joya == null) {
            throw new IllegalArgumentException(
                    "La joya es obligatoria"
            );
        }
        this.joya = joya;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null ||
                precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario es obligatorio y no puede ser negativo"
            );
        }
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(
                BigDecimal.valueOf(cantidad)
        );
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "idDetalle=" + idDetalle +
                ", idPedido=" +
                (pedido == null ? null : pedido.getIdPedido()) +
                ", idJoya=" +
                (joya == null ? null : joya.getIdJoya()) +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + calcularSubtotal() +
                '}';
    }
}