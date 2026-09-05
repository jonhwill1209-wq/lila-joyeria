package com.lilajoyeria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {

    private int idPedido;
    private Usuario usuario;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private EstadoPedido estado;

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

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", usuario=" + usuario +
                ", fechaPedido=" + fechaPedido +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }
}