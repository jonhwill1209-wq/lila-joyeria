package com.lilajoyeria.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_pedido")
    private int idPedido;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    @Column(name = "nombre_contacto", nullable = false, length = 100)
    private String nombreContacto;
    @Column(name = "direccion_entrega", nullable = false, length = 255)
    private String direccionEntrega;
    @Column(name = "telefono_whatsapp", nullable = false, length = 30)
    private String telefonoWhatsapp;
    @Enumerated(EnumType.STRING) @Column(length = 9)
    private EstadoPedido estado;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("idDetalle ASC")
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

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = validarTextoEntrega(
                nombreContacto, "El nombre de contacto", 100
        );
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = validarTextoEntrega(
                direccionEntrega, "La dirección de entrega", 255
        );
    }

    public String getTelefonoWhatsapp() {
        return telefonoWhatsapp;
    }

    public void setTelefonoWhatsapp(String telefonoWhatsapp) {
        this.telefonoWhatsapp = validarTextoEntrega(
                telefonoWhatsapp, "El teléfono de WhatsApp", 30
        );
    }

    private static String validarTextoEntrega(
            String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    campo + " no puede superar " + longitudMaxima
                            + " caracteres"
            );
        }
        return limpio;
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
        this.detalles.clear();

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