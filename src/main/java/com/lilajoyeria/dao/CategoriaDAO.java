package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private static final String SQL_LISTAR =
            "SELECT id_categoria, nombre " +
                    "FROM categorias ORDER BY nombre";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_categoria, nombre " +
                    "FROM categorias WHERE id_categoria = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO categorias (nombre) VALUES (?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE categorias SET nombre = ? " +
                    "WHERE id_categoria = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM categorias WHERE id_categoria = ?";

    public List<Categoria> listar() throws SQLException {

        List<Categoria> categorias = new ArrayList<>();

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(SQL_LISTAR);

             ResultSet resultado =
                     sentencia.executeQuery()) {

            while (resultado.next()) {
                categorias.add(
                        convertirEnCategoria(resultado)
                );
            }
        }

        return categorias;
    }

    public Categoria buscarPorId(int idCategoria)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_ID
                     )) {

            sentencia.setInt(1, idCategoria);

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    return convertirEnCategoria(resultado);
                }
            }
        }

        return null;
    }

    public int insertar(Categoria categoria)
            throws SQLException {

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria"
            );
        }

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_INSERTAR,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            sentencia.setString(
                    1,
                    categoria.getNombre()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                        "No fue posible insertar la categoría"
                );
            }

            try (ResultSet claves =
                         sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    int idGenerado = claves.getInt(1);

                    categoria.setIdCategoria(idGenerado);

                    return idGenerado;
                }
            }
        }

        throw new SQLException(
                "No se obtuvo el identificador generado"
        );
    }

    public boolean actualizar(Categoria categoria)
            throws SQLException {

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria"
            );
        }

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ACTUALIZAR
                     )) {

            sentencia.setString(
                    1,
                    categoria.getNombre()
            );

            sentencia.setInt(
                    2,
                    categoria.getIdCategoria()
            );

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idCategoria)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ELIMINAR
                     )) {

            sentencia.setInt(1, idCategoria);

            return sentencia.executeUpdate() > 0;
        }
    }

    private Categoria convertirEnCategoria(
            ResultSet resultado) throws SQLException {

        return new Categoria(
                resultado.getInt("id_categoria"),
                resultado.getString("nombre")
        );
    }
}