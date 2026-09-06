package com.lilajoyeria.dao;

import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String SQL_LISTAR =
            "SELECT id_usuario, nombre, email, " +
                    "password, rol FROM usuarios " +
                    "ORDER BY nombre";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_usuario, nombre, email, " +
                    "password, rol FROM usuarios " +
                    "WHERE id_usuario = ?";

    private static final String SQL_BUSCAR_POR_EMAIL =
            "SELECT id_usuario, nombre, email, " +
                    "password, rol FROM usuarios " +
                    "WHERE email = ?";

    private static final String SQL_VALIDAR_LOGIN =
            "SELECT id_usuario, nombre, email, " +
                    "password, rol FROM usuarios " +
                    "WHERE email = ? AND password = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO usuarios " +
                    "(nombre, email, password, rol) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE usuarios SET nombre = ?, " +
                    "email = ?, password = ?, rol = ? " +
                    "WHERE id_usuario = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM usuarios " +
                    "WHERE id_usuario = ?";

    public List<Usuario> listar()
            throws SQLException {

        List<Usuario> usuarios =
                new ArrayList<>();

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(SQL_LISTAR);

             ResultSet resultado =
                     sentencia.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(
                        convertirEnUsuario(resultado)
                );
            }
        }

        return usuarios;
    }

    public Usuario buscarPorId(int idUsuario)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_ID
                     )) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    return convertirEnUsuario(resultado);
                }
            }
        }

        return null;
    }

    public Usuario buscarPorEmail(String email)
            throws SQLException {

        if (email == null ||
                email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El correo electrónico es obligatorio"
            );
        }

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_EMAIL
                     )) {

            sentencia.setString(
                    1,
                    email.trim()
            );

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    return convertirEnUsuario(resultado);
                }
            }
        }

        return null;
    }

    public Usuario validarLogin(
            String email,
            String password) throws SQLException {

        if (email == null ||
                email.trim().isEmpty() ||
                password == null ||
                password.isEmpty()) {

            return null;
        }

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_VALIDAR_LOGIN
                     )) {

            sentencia.setString(
                    1,
                    email.trim()
            );

            sentencia.setString(
                    2,
                    password
            );

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    return convertirEnUsuario(resultado);
                }
            }
        }

        return null;
    }

    public int insertar(Usuario usuario)
            throws SQLException {

        validarUsuario(usuario);

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_INSERTAR,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            establecerParametros(
                    sentencia,
                    usuario
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                        "No fue posible insertar el usuario"
                );
            }

            try (ResultSet claves =
                         sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    int idGenerado =
                            claves.getInt(1);

                    usuario.setIdUsuario(idGenerado);

                    return idGenerado;
                }
            }
        }

        throw new SQLException(
                "No se obtuvo el identificador generado"
        );
    }

    public boolean actualizar(Usuario usuario)
            throws SQLException {

        validarUsuario(usuario);

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ACTUALIZAR
                     )) {

            establecerParametros(
                    sentencia,
                    usuario
            );

            sentencia.setInt(
                    5,
                    usuario.getIdUsuario()
            );

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idUsuario)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ELIMINAR
                     )) {

            sentencia.setInt(1, idUsuario);

            return sentencia.executeUpdate() > 0;
        }
    }

    private void establecerParametros(
            PreparedStatement sentencia,
            Usuario usuario) throws SQLException {

        sentencia.setString(
                1,
                usuario.getNombre()
        );

        sentencia.setString(
                2,
                usuario.getEmail()
        );

        sentencia.setString(
                3,
                usuario.getPassword()
        );

        sentencia.setString(
                4,
                usuario.getRol().name()
        );
    }

    private void validarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio"
            );
        }
    }

    private Usuario convertirEnUsuario(
            ResultSet resultado) throws SQLException {

        return new Usuario(
                resultado.getInt("id_usuario"),
                resultado.getString("nombre"),
                resultado.getString("email"),
                resultado.getString("password"),
                RolUsuario.valueOf(
                        resultado.getString("rol")
                )
        );
    }
}