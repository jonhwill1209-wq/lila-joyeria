package com.lilajoyeria.dao;

import com.lilajoyeria.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PersistenciaJPATest {
    private EntityManagerFactory fabrica;
    private CategoriaDAO categorias;
    private UsuarioDAO usuarios;
    private JoyaDAO joyas;
    private PedidoDAO pedidos;

    @BeforeEach
    void iniciar() {
        fabrica = Persistence.createEntityManagerFactory("lilaJoyeriaPU", Map.of(
                "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:lila;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "jakarta.persistence.jdbc.user", "sa",
                "jakarta.persistence.jdbc.password", "",
                "hibernate.hbm2ddl.auto", "create-drop"));
        categorias = new CategoriaDAO(fabrica);
        usuarios = new UsuarioDAO(fabrica);
        joyas = new JoyaDAO(fabrica);
        pedidos = new PedidoDAO(fabrica);
    }
    @AfterEach
    void cerrar() { if (fabrica != null) fabrica.close(); }

    private Usuario usuario() throws SQLException {
        Usuario u = new Usuario("Cliente", "cliente@lila.com", "Clave123", RolUsuario.CLIENTE);
        usuarios.insertar(u);
        return u;
    }
    private Joya joya() throws SQLException {
        Categoria c = new Categoria("Anillos");
        categorias.insertar(c);
        Joya j = new Joya("Anillo", "Descripción", "Oro", new BigDecimal("18.00"),
                new BigDecimal("75.00"), 10, null, c);
        joyas.insertar(j);
        return j;
    }
    private Pedido pedido(Usuario u, Joya j) {
        Pedido p = new Pedido();
        p.setUsuario(u);
        p.agregarDetalle(new DetallePedido(p, j, 2, j.getPrecio()));
        return p;
    }

    @Test
    void crudYConsultasParametrizadas() throws SQLException {
        Usuario u = usuario();
        assertEquals(u.getIdUsuario(), usuarios.buscarPorEmail(u.getEmail()).getIdUsuario());
        assertNull(usuarios.validarLogin("' OR 1=1 --", "Clave123"));
        assertNotNull(usuarios.validarLogin(u.getEmail(), u.getPassword()));
        u.setNombre("Editado");
        assertTrue(usuarios.actualizar(u));
        assertEquals("Editado", usuarios.buscarPorId(u.getIdUsuario()).getNombre());
        Joya j = joya();
        assertEquals("Anillos", joyas.buscarPorId(j.getIdJoya()).getCategoria().getNombre());
        assertEquals(1, joyas.obtenerJoyasPorCategoria(j.getCategoria().getIdCategoria()).size());
        j.setPrecio(new BigDecimal("80.00"));
        assertTrue(joyas.actualizar(j));
        assertEquals(0, new BigDecimal("80.00").compareTo(joyas.buscarPorId(j.getIdJoya()).getPrecio()));
        Categoria c = j.getCategoria();
        c.setNombre("Actualizada");
        assertTrue(categorias.actualizar(c));
        assertEquals("Actualizada", categorias.buscarPorId(c.getIdCategoria()).getNombre());
        assertTrue(joyas.eliminar(j.getIdJoya()));
        assertTrue(categorias.eliminar(c.getIdCategoria()));
        assertTrue(usuarios.eliminar(u.getIdUsuario()));
        assertFalse(usuarios.actualizar(u));
        assertFalse(usuarios.eliminar(u.getIdUsuario()));
        assertNull(usuarios.buscarPorId(u.getIdUsuario()));
    }

    @Test
    void agregadoRelacionesEdicionYEliminacion() throws SQLException {
        Pedido p = pedido(usuario(), joya());
        pedidos.guardar(p);
        Pedido recuperado = pedidos.buscarPorId(p.getIdPedido());
        assertEquals("Cliente", recuperado.getUsuario().getNombre());
        assertEquals("Anillos", recuperado.getDetalles().get(0).getJoya().getCategoria().getNombre());
        assertEquals(0, new BigDecimal("150.00").compareTo(recuperado.getTotal()));
        assertEquals(1, pedidos.listarPorUsuario(p.getUsuario().getIdUsuario()).size());
        assertTrue(pedidos.actualizarEstado(p.getIdPedido(), EstadoPedido.PAGADO));
        recuperado = pedidos.buscarPorId(p.getIdPedido());
        assertEquals(EstadoPedido.PAGADO, recuperado.getEstado());
        recuperado.getDetalles().get(0).setCantidad(3);
        assertTrue(pedidos.actualizar(recuperado));
        assertEquals(0, new BigDecimal("225.00").compareTo(pedidos.buscarPorId(p.getIdPedido()).getTotal()));
        recuperado = pedidos.buscarPorId(p.getIdPedido());
        recuperado.agregarDetalle(new DetallePedido(recuperado, recuperado.getDetalles().get(0).getJoya(),
                1, new BigDecimal("75.00")));
        assertTrue(pedidos.actualizar(recuperado));
        recuperado = pedidos.buscarPorId(p.getIdPedido());
        assertEquals(2, recuperado.getDetalles().size());
        recuperado.eliminarDetalle(recuperado.getDetalles().get(1));
        assertTrue(pedidos.actualizar(recuperado));
        assertEquals(1, pedidos.buscarPorId(p.getIdPedido()).getDetalles().size());
        var verificacion = fabrica.createEntityManager();
        try { assertEquals(1L, verificacion.createQuery("SELECT COUNT(d) FROM DetallePedido d", Long.class).getSingleResult()); }
        finally { verificacion.close(); }
        assertTrue(pedidos.eliminar(p.getIdPedido()));
        assertTrue(pedidos.listarTodos().isEmpty());
        var em = fabrica.createEntityManager();
        try { assertEquals(0L, em.createQuery("SELECT COUNT(d) FROM DetallePedido d", Long.class).getSingleResult()); }
        finally { em.close(); }
        assertNotNull(usuarios.buscarPorId(p.getUsuario().getIdUsuario()));
        assertNotNull(joyas.buscarPorId(p.getDetalles().get(0).getJoya().getIdJoya()));
    }

    @Test
    void rollbackNoDejaPedidoNiDetallesParciales() throws SQLException {
        Usuario u = usuario();
        Joya j = joya();
        Pedido p = pedido(u, j);
        Joya inexistente = new Joya(Integer.MAX_VALUE, "Inexistente", null, "Oro", null,
                BigDecimal.ONE, 1, null, null);
        p.agregarDetalle(new DetallePedido(p, inexistente, 1, BigDecimal.ONE));
        assertThrows(SQLException.class, () -> pedidos.guardar(p));
        assertTrue(pedidos.listarTodos().isEmpty());
        var em = fabrica.createEntityManager();
        try { assertEquals(0L, em.createQuery("SELECT COUNT(d) FROM DetallePedido d", Long.class).getSingleResult()); }
        finally { em.close(); }
    }

    @Test
    void integridadUnicidadYClavesForaneas() throws SQLException {
        Usuario u = usuario();
        assertThrows(SQLException.class, this::usuario);
        Pedido p = pedido(u, joya());
        pedidos.guardar(p);
        assertThrows(SQLException.class, () -> usuarios.eliminar(u.getIdUsuario()));
        assertNotNull(usuarios.buscarPorId(u.getIdUsuario()));
        assertEquals(1, pedidos.listarTodos().size());
    }
}
