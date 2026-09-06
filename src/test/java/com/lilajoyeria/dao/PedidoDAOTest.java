package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.DetallePedido;
import com.lilajoyeria.model.EstadoPedido;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.model.Pedido;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PedidoDAOTest {

    public static void main(String[] args) {

        CategoriaDAO categoriaDAO =
                new CategoriaDAO();

        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        JoyaDAO joyaDAO =
                new JoyaDAO();

        PedidoDAO pedidoDAO =
                new PedidoDAO();

        int idUsuarioTemporal = 0;
        int idJoyaTemporal = 0;
        int idPedidoTemporal = 0;

        try {
            List<Categoria> categorias =
                    categoriaDAO.listar();

            comprobar(
                    !categorias.isEmpty(),
                    "Debe existir una categoría"
            );

            Categoria categoria =
                    categorias.get(0);

            String emailTemporal =
                    "pedido" +
                            System.currentTimeMillis() +
                            "@lilajoyeria.com";

            Usuario usuario = new Usuario(
                    "Cliente temporal",
                    emailTemporal,
                    "ClaveTemporal123",
                    RolUsuario.CLIENTE
            );

            idUsuarioTemporal =
                    usuarioDAO.insertar(usuario);

            System.out.println(
                    "CORRECTO: usuario temporal creado"
            );

            Joya joya = new Joya(
                    "Joya temporal para pedido",
                    "Registro de prueba",
                    "Oro",
                    new BigDecimal("18.00"),
                    new BigDecimal("75.00"),
                    10,
                    categoria
            );

            idJoyaTemporal =
                    joyaDAO.insertar(joya);

            System.out.println(
                    "CORRECTO: joya temporal creada"
            );

            Pedido pedido = new Pedido();

            pedido.setUsuario(usuario);

            DetallePedido detalle =
                    new DetallePedido(
                            pedido,
                            joya,
                            2,
                            new BigDecimal("75.00")
                    );

            pedido.agregarDetalle(detalle);

            comprobar(
                    pedido.getTotal().compareTo(
                            new BigDecimal("150.00")
                    ) == 0,
                    "El total debe ser 150.00"
            );

            idPedidoTemporal =
                    pedidoDAO.guardar(pedido);

            comprobar(
                    idPedidoTemporal > 0,
                    "El pedido debe recibir un ID"
            );

            System.out.println(
                    "CORRECTO: pedido y detalle guardados"
            );

            Pedido encontrado =
                    pedidoDAO.buscarPorId(
                            idPedidoTemporal
                    );

            comprobar(
                    encontrado != null,
                    "El pedido debe encontrarse"
            );

            comprobar(
                    encontrado.getDetalles().size() == 1,
                    "El pedido debe contener un detalle"
            );

            comprobar(
                    encontrado.getTotal().compareTo(
                            new BigDecimal("150.00")
                    ) == 0,
                    "El total recuperado debe ser 150.00"
            );

            System.out.println(
                    "CORRECTO: pedido recuperado con detalles"
            );

            comprobar(
                    contienePedido(
                            pedidoDAO.listarPorUsuario(
                                    idUsuarioTemporal
                            ),
                            idPedidoTemporal
                    ),
                    "El pedido debe aparecer para el usuario"
            );

            System.out.println(
                    "CORRECTO: listado por usuario"
            );

            comprobar(
                    contienePedido(
                            pedidoDAO.listarTodos(),
                            idPedidoTemporal
                    ),
                    "El pedido debe aparecer en el listado"
            );

            System.out.println(
                    "CORRECTO: listado general"
            );

            comprobar(
                    pedidoDAO.actualizarEstado(
                            idPedidoTemporal,
                            EstadoPedido.PAGADO
                    ),
                    "El estado debe actualizarse"
            );

            Pedido actualizado =
                    pedidoDAO.buscarPorId(
                            idPedidoTemporal
                    );

            comprobar(
                    actualizado != null &&
                            actualizado.getEstado() ==
                                    EstadoPedido.PAGADO,
                    "El estado debe ser PAGADO"
            );

            System.out.println(
                    "CORRECTO: estado actualizado"
            );

            probarRollback(
                    pedidoDAO,
                    usuario,
                    categoria
            );

            comprobar(
                    pedidoDAO.eliminar(
                            idPedidoTemporal
                    ),
                    "El pedido debe eliminarse"
            );

            comprobar(
                    pedidoDAO.buscarPorId(
                            idPedidoTemporal
                    ) == null,
                    "El pedido ya no debe existir"
            );

            idPedidoTemporal = 0;

            System.out.println(
                    "CORRECTO: pedido y detalles eliminados"
            );

            comprobar(
                    joyaDAO.eliminar(idJoyaTemporal),
                    "La joya temporal debe eliminarse"
            );

            idJoyaTemporal = 0;

            comprobar(
                    usuarioDAO.eliminar(
                            idUsuarioTemporal
                    ),
                    "El usuario temporal debe eliminarse"
            );

            idUsuarioTemporal = 0;

            System.out.println(
                    "CORRECTO: registros temporales eliminados"
            );

            System.out.println(
                    "Todas las pruebas de PedidoDAO " +
                            "finalizaron correctamente"
            );

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "Falló la prueba de PedidoDAO",
                    exception
            );

        } finally {

            eliminarRegistrosTemporales(
                    pedidoDAO,
                    joyaDAO,
                    usuarioDAO,
                    idPedidoTemporal,
                    idJoyaTemporal,
                    idUsuarioTemporal
            );
        }
    }

    private static void probarRollback(
            PedidoDAO pedidoDAO,
            Usuario usuario,
            Categoria categoria) throws SQLException {

        Joya joyaInexistente = new Joya(
                Integer.MAX_VALUE,
                "Joya inexistente",
                "Prueba de rollback",
                "Oro",
                new BigDecimal("18.00"),
                new BigDecimal("10.00"),
                1,
                categoria
        );

        Pedido pedidoFallido =
                new Pedido();

        pedidoFallido.setUsuario(usuario);

        DetallePedido detalleFallido =
                new DetallePedido(
                        pedidoFallido,
                        joyaInexistente,
                        1,
                        new BigDecimal("10.00")
                );

        pedidoFallido.agregarDetalle(
                detalleFallido
        );

        boolean falloEsperado = false;

        try {
            pedidoDAO.guardar(pedidoFallido);

        } catch (SQLException exception) {
            falloEsperado = true;
        }

        comprobar(
                falloEsperado,
                "La transacción inválida debe fallar"
        );

        if (pedidoFallido.getIdPedido() > 0) {
            comprobar(
                    pedidoDAO.buscarPorId(
                            pedidoFallido.getIdPedido()
                    ) == null,
                    "El pedido fallido debe revertirse"
            );
        }

        System.out.println(
                "CORRECTO: rollback comprobado"
        );
    }

    private static boolean contienePedido(
            List<Pedido> pedidos,
            int idPedido) {

        for (Pedido pedido : pedidos) {
            if (pedido.getIdPedido() == idPedido) {
                return true;
            }
        }

        return false;
    }

    private static void comprobar(
            boolean condicion,
            String mensaje) {

        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void eliminarRegistrosTemporales(
            PedidoDAO pedidoDAO,
            JoyaDAO joyaDAO,
            UsuarioDAO usuarioDAO,
            int idPedido,
            int idJoya,
            int idUsuario) {

        try {
            if (idPedido > 0 &&
                    pedidoDAO.buscarPorId(idPedido) != null) {

                pedidoDAO.eliminar(idPedido);
            }

            if (idJoya > 0 &&
                    joyaDAO.buscarPorId(idJoya) != null) {

                joyaDAO.eliminar(idJoya);
            }

            if (idUsuario > 0 &&
                    usuarioDAO.buscarPorId(idUsuario) != null) {

                usuarioDAO.eliminar(idUsuario);
            }

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "No se pudieron limpiar " +
                            "los registros temporales",
                    exception
            );
        }
    }
}