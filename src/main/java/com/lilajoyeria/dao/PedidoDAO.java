package com.lilajoyeria.dao;

import com.lilajoyeria.model.*;
import jakarta.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Pedido es la raíz del agregado: detalles se escriben y eliminan con él. */
public class PedidoDAO extends JpaDAO {
    private static final String CONSULTA = "SELECT DISTINCT p FROM Pedido p JOIN FETCH p.usuario "
            + "LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.joya j LEFT JOIN FETCH j.categoria";
    public PedidoDAO() { super(); }
    public PedidoDAO(EntityManagerFactory fabrica) { super(fabrica); }

    public int guardar(Pedido pedido) throws SQLException {
        validar(pedido);
        if (pedido.getIdPedido() != 0) throw new IllegalArgumentException("El pedido ya tiene identificador");
        pedido.calcularTotal();
        return ejecutar(true, em -> {
            pedido.setUsuario(em.getReference(Usuario.class, pedido.getUsuario().getIdUsuario()));
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);
                detalle.setJoya(em.getReference(Joya.class, detalle.getJoya().getIdJoya()));
            }
            em.persist(pedido);
            em.flush();
            return pedido.getIdPedido();
        });
    }
    public Pedido buscarPorId(int idPedido) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE p.idPedido = :id", Pedido.class)
                .setParameter("id", idPedido).getResultStream().findFirst().orElse(null));
    }
    public List<Pedido> listarTodos() throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " ORDER BY p.fechaPedido DESC", Pedido.class).getResultList());
    }
    public List<Pedido> listarPorUsuario(int idUsuario) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE p.usuario.idUsuario = :id ORDER BY p.fechaPedido DESC", Pedido.class)
                .setParameter("id", idUsuario).getResultList());
    }
    public boolean actualizarEstado(int idPedido, EstadoPedido estado) throws SQLException {
        Objects.requireNonNull(estado, "El estado es obligatorio");
        return ejecutar(true, em -> em.createQuery("UPDATE Pedido p SET p.estado = :estado WHERE p.idPedido = :id")
                .setParameter("estado", estado).setParameter("id", idPedido).executeUpdate() > 0);
    }
    public boolean actualizar(Pedido pedido) throws SQLException {
        validar(pedido);
        pedido.calcularTotal();
        return ejecutar(true, em -> {
            if (em.find(Pedido.class, pedido.getIdPedido()) == null) return false;
            em.merge(pedido);
            return true;
        });
    }
    public boolean eliminar(int idPedido) throws SQLException {
        return ejecutar(true, em -> {
            Pedido pedido = em.find(Pedido.class, idPedido);
            if (pedido == null) return false;
            em.remove(pedido); // Cascade elimina detalles; nunca usuarios ni joyas.
            return true;
        });
    }
    private void validar(Pedido pedido) {
        if (pedido == null || pedido.getUsuario() == null || pedido.getUsuario().getIdUsuario() <= 0)
            throw new IllegalArgumentException("El pedido requiere un usuario persistido");
        if (pedido.getDetalles().isEmpty()) throw new IllegalArgumentException("El pedido requiere detalles");
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getJoya() == null || detalle.getJoya().getIdJoya() <= 0 || detalle.getCantidad() <= 0)
                throw new IllegalArgumentException("Cada detalle requiere joya persistida y cantidad positiva");
        }
    }
}
