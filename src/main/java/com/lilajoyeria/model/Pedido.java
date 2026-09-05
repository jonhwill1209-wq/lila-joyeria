package com.lilajoyeria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int idPedido;
    private Usuario usuario;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private EstadoPedido estado;
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public Pedido(Usuario usuario, BigDecimal total) {
        this();
        setUsuario(usuario);
        setTotal(total);
    }

    public Pedido(int idPedido, Usuario usuario,
                  LocalDateTime fechaPedido, BigDecimal total,
                  EstadoPedido estado) {
        setIdPedido(idPedido);
        setUsuario(usuario);
        setFechaPedido(fechaPedido);
        setTotal(total);
        setEstado(estado);
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        if (idPedido < 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no puede ser negativo"
            );
        }
        this.idPedido = idPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario del pedido es obligatorio"
            );
        }
        this.usuario = usuario;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        if (fechaPedido == null) {
            throw new IllegalArgumentException(
                    "La fecha del pedido es obligatoria"
            );
        }
        this.fechaPedido = fechaPedido;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        if (total == null ||
                total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El total es obligatorio y no puede ser negativo"
            );
        }
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del pedido es obligatorio"
            );
        }
        this.estado = estado;
    }

    public List<DetallePedido> getDetalles() {
        return new ArrayList<>(detalles);
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = new ArrayList<>();

        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                agregarDetalle(detalle);
            }
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El detalle del pedido no puede ser nulo"
            );
        }

        if (detalle.getPedido() != this) {
            detalle.setPedido(this);
        }

        detalles.add(detalle);
        calcularTotal();
    }

    public boolean eliminarDetalle(DetallePedido detalle) {
        boolean eliminado = detalles.remove(detalle);

        if (eliminado) {
            calcularTotal();
        }

        return eliminado;
    }

    public BigDecimal calcularTotal() {
        BigDecimal nuevoTotal = BigDecimal.ZERO;

        for (DetallePedido detalle : detalles) {
            nuevoTotal = nuevoTotal.add(
                    detalle.calcularSubtotal()
            );
        }

        this.total = nuevoTotal;
        return total;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", usuario=" + usuario +
                ", fechaPedido=" + fechaPedido +
                ", total=" + total +
                ", estado=" + estado +
                ", cantidadDetalles=" + detalles.size() +
                '}';
    }
}