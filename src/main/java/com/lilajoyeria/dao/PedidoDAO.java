package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.DetallePedido;
import com.lilajoyeria.model.EstadoPedido;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.model.Pedido;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private static final String CONSULTA_PEDIDO_BASE =
            "SELECT p.id_pedido, p.fecha_pedido, " +
                    "p.total, p.estado, " +
                    "u.id_usuario, " +
                    "u.nombre AS usuario_nombre, " +
                    "u.email, u.password, u.rol " +
                    "FROM pedidos p " +
                    "INNER JOIN usuarios u " +
                    "ON p.id_usuario = u.id_usuario ";

    private static final String SQL_BUSCAR_POR_ID =
            CONSULTA_PEDIDO_BASE +
                    "WHERE p.id_pedido = ?";

    private static final String SQL_LISTAR_TODOS =
            CONSULTA_PEDIDO_BASE +
                    "ORDER BY p.fecha_pedido DESC";

    private static final String SQL_LISTAR_POR_USUARIO =
            CONSULTA_PEDIDO_BASE +
                    "WHERE p.id_usuario = ? " +
                    "ORDER BY p.fecha_pedido DESC";

    private static final String SQL_BUSCAR_DETALLES =
            "SELECT d.id_detalle, d.cantidad, " +
                    "d.precio_unitario, " +
                    "j.id_joya, " +
                    "j.nombre AS joya_nombre, " +
                    "j.descripcion, j.material, " +
                    "j.quilates, j.precio, j.stock, " +
                    "c.id_categoria, " +
                    "c.nombre AS categoria_nombre " +
                    "FROM detalles_pedido d " +
                    "INNER JOIN joyas j " +
                    "ON d.id_joya = j.id_joya " +
                    "LEFT JOIN categorias c " +
                    "ON j.id_categoria = c.id_categoria " +
                    "WHERE d.id_pedido = ? " +
                    "ORDER BY d.id_detalle";

    private static final String SQL_INSERTAR_PEDIDO =
            "INSERT INTO pedidos " +
                    "(id_usuario, fecha_pedido, total, estado) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERTAR_DETALLE =
            "INSERT INTO detalles_pedido " +
                    "(id_pedido, id_joya, cantidad, " +
                    "precio_unitario) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR_ESTADO =
            "UPDATE pedidos SET estado = ? " +
                    "WHERE id_pedido = ?";

    private static final String SQL_ELIMINAR_DETALLES =
            "DELETE FROM detalles_pedido " +
                    "WHERE id_pedido = ?";

    private static final String SQL_ELIMINAR_PEDIDO =
            "DELETE FROM pedidos " +
                    "WHERE id_pedido = ?";

    public int guardar(Pedido pedido)
            throws SQLException {

        validarPedido(pedido);

        pedido.calcularTotal();

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int idPedido =
                        insertarPedido(
                                conexion,
                                pedido
                        );

                pedido.setIdPedido(idPedido);

                insertarDetalles(
                        conexion,
                        pedido
                );

                conexion.commit();

                return idPedido;

            } catch (SQLException |
                     RuntimeException exception) {

                intentarRollback(
                        conexion,
                        exception
                );

                throw exception;

            } finally {
                intentarRestaurarAutoCommit(conexion);
            }
        }
    }

    public Pedido buscarPorId(int idPedido)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_ID
                     )) {

            sentencia.setInt(1, idPedido);

            Pedido pedido = null;

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    pedido =
                            convertirEnPedido(resultado);
                }
            }

            if (pedido != null) {
                cargarDetalles(
                        conexion,
                        pedido
                );
            }

            return pedido;
        }
    }

    public List<Pedido> listarTodos()
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_LISTAR_TODOS
                     )) {

            return consultarPedidos(
                    conexion,
                    sentencia
            );
        }
    }

    public List<Pedido> listarPorUsuario(
            int idUsuario) throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_LISTAR_POR_USUARIO
                     )) {

            sentencia.setInt(1, idUsuario);

            return consultarPedidos(
                    conexion,
                    sentencia
            );
        }
    }

    public boolean actualizarEstado(
            int idPedido,
            EstadoPedido estado) throws SQLException {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio"
            );
        }

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ACTUALIZAR_ESTADO
                     )) {

            sentencia.setString(
                    1,
                    estado.name()
            );

            sentencia.setInt(
                    2,
                    idPedido
            );

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idPedido)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement sentenciaDetalles =
                             conexion.prepareStatement(
                                     SQL_ELIMINAR_DETALLES
                             )) {

                    sentenciaDetalles.setInt(
                            1,
                            idPedido
                    );

                    sentenciaDetalles.executeUpdate();
                }

                int filasAfectadas;

                try (PreparedStatement sentenciaPedido =
                             conexion.prepareStatement(
                                     SQL_ELIMINAR_PEDIDO
                             )) {

                    sentenciaPedido.setInt(
                            1,
                            idPedido
                    );

                    filasAfectadas =
                            sentenciaPedido.executeUpdate();
                }

                conexion.commit();

                return filasAfectadas > 0;

            } catch (SQLException |
                     RuntimeException exception) {

                intentarRollback(
                        conexion,
                        exception
                );

                throw exception;

            } finally {
                intentarRestaurarAutoCommit(conexion);
            }
        }
    }

    private int insertarPedido(
            Connection conexion,
            Pedido pedido) throws SQLException {

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_INSERTAR_PEDIDO,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            sentencia.setInt(
                    1,
                    pedido.getUsuario().getIdUsuario()
            );

            sentencia.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            pedido.getFechaPedido()
                    )
            );

            sentencia.setBigDecimal(
                    3,
                    pedido.getTotal()
            );

            sentencia.setString(
                    4,
                    pedido.getEstado().name()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                        "No fue posible guardar el pedido"
                );
            }

            try (ResultSet claves =
                         sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }

        throw new SQLException(
                "No se obtuvo el ID del pedido"
        );
    }

    private void insertarDetalles(
            Connection conexion,
            Pedido pedido) throws SQLException {

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_INSERTAR_DETALLE,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            for (DetallePedido detalle :
                    pedido.getDetalles()) {

                sentencia.setInt(
                        1,
                        pedido.getIdPedido()
                );

                sentencia.setInt(
                        2,
                        detalle.getJoya().getIdJoya()
                );

                sentencia.setInt(
                        3,
                        detalle.getCantidad()
                );

                sentencia.setBigDecimal(
                        4,
                        detalle.getPrecioUnitario()
                );

                int filasAfectadas =
                        sentencia.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException(
                            "No fue posible guardar " +
                                    "un detalle del pedido"
                    );
                }

                try (ResultSet claves =
                             sentencia.getGeneratedKeys()) {

                    if (claves.next()) {
                        detalle.setIdDetalle(
                                claves.getInt(1)
                        );
                    }
                }
            }
        }
    }

    private List<Pedido> consultarPedidos(
            Connection conexion,
            PreparedStatement sentencia)
            throws SQLException {

        List<Pedido> pedidos =
                new ArrayList<>();

        try (ResultSet resultado =
                     sentencia.executeQuery()) {

            while (resultado.next()) {
                pedidos.add(
                        convertirEnPedido(resultado)
                );
            }
        }

        for (Pedido pedido : pedidos) {
            cargarDetalles(
                    conexion,
                    pedido
            );
        }

        return pedidos;
    }

    private void cargarDetalles(
            Connection conexion,
            Pedido pedido) throws SQLException {

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_DETALLES
                     )) {

            sentencia.setInt(
                    1,
                    pedido.getIdPedido()
            );

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                while (resultado.next()) {

                    Categoria categoria =
                            convertirEnCategoria(resultado);

                    Joya joya = new Joya(
                            resultado.getInt("id_joya"),
                            resultado.getString(
                                    "joya_nombre"
                            ),
                            resultado.getString(
                                    "descripcion"
                            ),
                            resultado.getString(
                                    "material"
                            ),
                            resultado.getBigDecimal(
                                    "quilates"
                            ),
                            resultado.getBigDecimal(
                                    "precio"
                            ),
                            resultado.getInt("stock"),
                            categoria
                    );

                    DetallePedido detalle =
                            new DetallePedido(
                                    resultado.getInt(
                                            "id_detalle"
                                    ),
                                    pedido,
                                    joya,
                                    resultado.getInt(
                                            "cantidad"
                                    ),
                                    resultado.getBigDecimal(
                                            "precio_unitario"
                                    )
                            );

                    pedido.agregarDetalle(detalle);
                }
            }
        }
    }

    private Pedido convertirEnPedido(
            ResultSet resultado) throws SQLException {

        Usuario usuario = new Usuario(
                resultado.getInt("id_usuario"),
                resultado.getString(
                        "usuario_nombre"
                ),
                resultado.getString("email"),
                resultado.getString("password"),
                RolUsuario.valueOf(
                        resultado.getString("rol")
                )
        );

        return new Pedido(
                resultado.getInt("id_pedido"),
                usuario,
                resultado.getTimestamp(
                        "fecha_pedido"
                ).toLocalDateTime(),
                resultado.getBigDecimal("total"),
                EstadoPedido.valueOf(
                        resultado.getString("estado")
                )
        );
    }

    private Categoria convertirEnCategoria(
            ResultSet resultado) throws SQLException {

        int idCategoria =
                resultado.getInt("id_categoria");

        if (resultado.wasNull()) {
            return null;
        }

        return new Categoria(
                idCategoria,
                resultado.getString(
                        "categoria_nombre"
                )
        );
    }

    private void validarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido es obligatorio"
            );
        }

        if (pedido.getUsuario() == null ||
                pedido.getUsuario()
                        .getIdUsuario() <= 0) {

            throw new IllegalArgumentException(
                    "El usuario debe existir " +
                            "en la base de datos"
            );
        }

        if (pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener " +
                            "al menos un detalle"
            );
        }

        for (DetallePedido detalle :
                pedido.getDetalles()) {

            if (detalle.getJoya() == null ||
                    detalle.getJoya()
                            .getIdJoya() <= 0) {

                throw new IllegalArgumentException(
                        "Todas las joyas deben existir " +
                                "en la base de datos"
                );
            }
        }
    }

    private void intentarRollback(
            Connection conexion,
            Exception exception) {

        try {
            conexion.rollback();

        } catch (SQLException rollbackException) {
            exception.addSuppressed(
                    rollbackException
            );
        }
    }

    private void intentarRestaurarAutoCommit(
            Connection conexion) {

        try {
            conexion.setAutoCommit(true);

        } catch (SQLException ignored) {
            // La conexión se cerrará automáticamente.
        }
    }
}