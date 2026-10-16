package com.lilajoyeria.bean;

import com.lilajoyeria.dao.PedidoDAO;
import com.lilajoyeria.model.EstadoPedido;
import com.lilajoyeria.model.Pedido;
import com.lilajoyeria.util.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Tabla de pedidos según el rol:
 * CLIENTE ve solo sus pedidos (solo lectura); ADMIN ve todos y puede
 * cambiar el estado o eliminar pedidos PENDIENTES.
 */
@Named
@ViewScoped
public class PedidosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AuthBean authBean;

    private List<Pedido> todos = new ArrayList<>();
    private List<Pedido> visibles = new ArrayList<>();

    // Filtros
    private EstadoPedido filtroEstado;
    private String filtroCliente;

    @PostConstruct
    public void iniciar() {
        cargar();
    }

    public void cargar() {
        try {
            PedidoDAO dao = new PedidoDAO();

            if (authBean.isAdmin()) {
                todos = dao.listarTodos();
            } else if (authBean.isCliente()) {
                todos = dao.listarPorUsuario(
                        authBean.getUsuarioActual().getIdUsuario()
                );
            } else {
                todos = new ArrayList<>();
            }

            filtrar();
        } catch (SQLException e) {
            FacesUtil.error("No fue posible cargar los pedidos.");
        }
    }

    public void filtrar() {
        String buscado = filtroCliente == null
                ? ""
                : filtroCliente.trim().toLowerCase(Locale.ROOT);

        visibles = todos.stream()
                .filter(p -> filtroEstado == null
                        || p.getEstado() == filtroEstado)
                .filter(p -> buscado.isEmpty()
                        || p.getUsuario().getNombre()
                        .toLowerCase(Locale.ROOT).contains(buscado)
                        || p.getUsuario().getEmail()
                        .toLowerCase(Locale.ROOT).contains(buscado))
                .collect(Collectors.toList());
    }

    public void limpiarFiltros() {
        filtroEstado = null;
        filtroCliente = null;
        filtrar();
    }

    /** Guarda el estado elegido en la fila (solo ADMIN). */
    public void guardarEstado(Pedido pedido) {
        if (!authBean.isAdmin()) {
            FacesUtil.error("No tienes permiso para modificar pedidos.");
            return;
        }

        try {
            if (new PedidoDAO().actualizarEstado(
                    pedido.getIdPedido(), pedido.getEstado())) {
                FacesUtil.info("Pedido #" + pedido.getIdPedido()
                        + " actualizado a " + pedido.getEstado() + ".");
            } else {
                FacesUtil.error("El pedido ya no existe.");
            }
        } catch (SQLException e) {
            FacesUtil.error("No fue posible actualizar el pedido.");
        }

        cargar();
    }

    /**
     * Elimina un pedido PENDIENTE (solo ADMIN). Los pedidos pagados o
     * enviados se conservan como historial.
     */
    public void eliminar(Pedido pedido) {
        if (!authBean.isAdmin()) {
            FacesUtil.error("No tienes permiso para eliminar pedidos.");
            return;
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            FacesUtil.error("Solo se pueden eliminar pedidos PENDIENTES.");
            cargar();
            return;
        }

        try {
            if (new PedidoDAO().eliminar(pedido.getIdPedido())) {
                FacesUtil.info("Pedido #" + pedido.getIdPedido() + " eliminado.");
            } else {
                FacesUtil.error("El pedido ya no existe.");
            }
        } catch (SQLException e) {
            FacesUtil.error("No fue posible eliminar el pedido.");
        }

        cargar();
    }

    public EstadoPedido[] getEstados() {
        return EstadoPedido.values();
    }

    public List<Pedido> getPedidos() {
        return visibles;
    }

    public EstadoPedido getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(EstadoPedido filtroEstado) {
        this.filtroEstado = filtroEstado;
    }

    public String getFiltroCliente() {
        return filtroCliente;
    }

    public void setFiltroCliente(String filtroCliente) {
        this.filtroCliente = filtroCliente;
    }
}
