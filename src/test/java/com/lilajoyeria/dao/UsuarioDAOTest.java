package com.lilajoyeria.dao;

import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioDAOTest {

    private static final String PASSWORD_PRUEBA =
            "ClaveTemporal123";

    public static void main(String[] args) {

        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        int idUsuarioTemporal = 0;

        String emailTemporal =
                "prueba" +
                        System.currentTimeMillis() +
                        "@lilajoyeria.com";

        try {
            Usuario usuario = new Usuario(
                    "Usuario temporal",
                    emailTemporal,
                    PASSWORD_PRUEBA,
                    RolUsuario.CLIENTE
            );

            idUsuarioTemporal =
                    usuarioDAO.insertar(usuario);

            comprobar(
                    idUsuarioTemporal > 0,
                    "El usuario debe recibir un ID"
            );

            System.out.println(
                    "CORRECTO: usuario insertado"
            );

            Usuario encontradoPorId =
                    usuarioDAO.buscarPorId(
                            idUsuarioTemporal
                    );

            comprobar(
                    encontradoPorId != null,
                    "El usuario debe encontrarse por ID"
            );

            System.out.println(
                    "CORRECTO: búsqueda por ID"
            );

            Usuario encontradoPorEmail =
                    usuarioDAO.buscarPorEmail(
                            emailTemporal
                    );

            comprobar(
                    encontradoPorEmail != null,
                    "El usuario debe encontrarse por email"
            );

            System.out.println(
                    "CORRECTO: búsqueda por email"
            );

            Usuario loginCorrecto =
                    usuarioDAO.validarLogin(
                            emailTemporal,
                            PASSWORD_PRUEBA
                    );

            comprobar(
                    loginCorrecto != null &&
                            loginCorrecto.getIdUsuario() ==
                                    idUsuarioTemporal,
                    "El login correcto debe aceptarse"
            );

            System.out.println(
                    "CORRECTO: login válido aceptado"
            );

            Usuario loginIncorrecto =
                    usuarioDAO.validarLogin(
                            emailTemporal,
                            "PasswordIncorrecto"
                    );

            comprobar(
                    loginIncorrecto == null,
                    "El login incorrecto debe rechazarse"
            );

            System.out.println(
                    "CORRECTO: login inválido rechazado"
            );

            usuario.setNombre(
                    "Usuario temporal actualizado"
            );

            usuario.setRol(RolUsuario.ADMIN);

            comprobar(
                    usuarioDAO.actualizar(usuario),
                    "El usuario debe actualizarse"
            );

            Usuario actualizado =
                    usuarioDAO.buscarPorId(
                            idUsuarioTemporal
                    );

            comprobar(
                    actualizado != null &&
                            actualizado.getNombre().equals(
                                    "Usuario temporal actualizado"
                            ) &&
                            actualizado.getRol() ==
                                    RolUsuario.ADMIN,
                    "Los cambios deben guardarse"
            );

            System.out.println(
                    "CORRECTO: usuario actualizado"
            );

            comprobar(
                    contieneUsuario(
                            usuarioDAO.listar(),
                            idUsuarioTemporal
                    ),
                    "El usuario debe aparecer en el listado"
            );

            System.out.println(
                    "CORRECTO: listado de usuarios"
            );

            comprobar(
                    usuarioDAO.eliminar(
                            idUsuarioTemporal
                    ),
                    "El usuario debe eliminarse"
            );

            comprobar(
                    usuarioDAO.buscarPorId(
                            idUsuarioTemporal
                    ) == null,
                    "El usuario ya no debe existir"
            );

            System.out.println(
                    "CORRECTO: usuario temporal eliminado"
            );

            System.out.println(
                    "Todas las pruebas de UsuarioDAO " +
                            "finalizaron correctamente"
            );

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "Falló la prueba de UsuarioDAO",
                    exception
            );

        } finally {

            eliminarRegistroTemporal(
                    usuarioDAO,
                    idUsuarioTemporal
            );
        }
    }

    private static boolean contieneUsuario(
            List<Usuario> usuarios,
            int idUsuario) {

        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario() == idUsuario) {
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

    private static void eliminarRegistroTemporal(
            UsuarioDAO usuarioDAO,
            int idUsuario) {

        if (idUsuario <= 0) {
            return;
        }

        try {
            if (usuarioDAO.buscarPorId(idUsuario) != null) {
                usuarioDAO.eliminar(idUsuario);

                System.out.println(
                        "LIMPIEZA: se eliminó el usuario temporal"
                );
            }

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "No se pudo eliminar el usuario temporal",
                    exception
            );
        }
    }
}