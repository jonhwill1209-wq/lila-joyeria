package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class JoyaDAO {

    private static final String CONSULTA_BASE =
            "SELECT j.id_joya, " +
                    "j.nombre AS nombre_joya, " +
                    "j.descripcion, " +
                    "j.material, " +
                    "j.quilates, " +
                    "j.precio, " +
                    "j.stock, " +
                    "c.id_categoria, " +
                    "c.nombre AS nombre_categoria " +
                    "FROM joyas j " +
                    "LEFT JOIN categorias c " +
                    "ON j.id_categoria = c.id_categoria ";

    private static final String SQL_LISTAR =
            CONSULTA_BASE +
                    "ORDER BY j.nombre";

    private static final String SQL_BUSCAR_POR_ID =
            CONSULTA_BASE +
                    "WHERE j.id_joya = ?";

    private static final String SQL_BUSCAR_POR_CATEGORIA =
            CONSULTA_BASE +
                    "WHERE j.id_categoria = ? " +
                    "ORDER BY j.nombre";

    private static final String SQL_INSERTAR =
            "INSERT INTO joyas " +
                    "(nombre, descripcion, material, quilates, " +
                    "precio, stock, id_categoria) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE joyas SET " +
                    "nombre = ?, descripcion = ?, material = ?, " +
                    "quilates = ?, precio = ?, stock = ?, " +
                    "id_categoria = ? " +
                    "WHERE id_joya = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM joyas WHERE id_joya = ?";

    public List<Joya> listar() throws SQLException {

        List<Joya> joyas = new ArrayList<>();

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(SQL_LISTAR);

             ResultSet resultado =
                     sentencia.executeQuery()) {

            while (resultado.next()) {
                joyas.add(convertirEnJoya(resultado));
            }
        }

        return joyas;
    }

    public Joya buscarPorId(int idJoya)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_ID
                     )) {

            sentencia.setInt(1, idJoya);

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                if (resultado.next()) {
                    return convertirEnJoya(resultado);
                }
            }
        }

        return null;
    }

    public List<Joya> obtenerJoyasPorCategoria(
            int idCategoria) throws SQLException {

        List<Joya> joyas = new ArrayList<>();

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_BUSCAR_POR_CATEGORIA
                     )) {

            sentencia.setInt(1, idCategoria);

            try (ResultSet resultado =
                         sentencia.executeQuery()) {

                while (resultado.next()) {
                    joyas.add(
                            convertirEnJoya(resultado)
                    );
                }
            }
        }

        return joyas;
    }

    public int insertar(Joya joya)
            throws SQLException {

        validarJoya(joya);

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_INSERTAR,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            establecerParametros(sentencia, joya);

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                        "No fue posible insertar la joya"
                );
            }

            try (ResultSet claves =
                         sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    int idGenerado = claves.getInt(1);

                    joya.setIdJoya(idGenerado);

                    return idGenerado;
                }
            }
        }

        throw new SQLException(
                "No se obtuvo el identificador generado"
        );
    }

    public boolean actualizar(Joya joya)
            throws SQLException {

        validarJoya(joya);

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ACTUALIZAR
                     )) {

            establecerParametros(sentencia, joya);

            sentencia.setInt(
                    8,
                    joya.getIdJoya()
            );

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idJoya)
            throws SQLException {

        try (Connection conexion =
                     ConexionBD.obtenerConexion();

             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             SQL_ELIMINAR
                     )) {

            sentencia.setInt(1, idJoya);

            return sentencia.executeUpdate() > 0;
        }
    }

    private void establecerParametros(
            PreparedStatement sentencia,
            Joya joya) throws SQLException {

        sentencia.setString(
                1,
                joya.getNombre()
        );

        sentencia.setString(
                2,
                joya.getDescripcion()
        );

        sentencia.setString(
                3,
                joya.getMaterial()
        );

        if (joya.getQuilates() == null) {
            sentencia.setNull(4, Types.DECIMAL);
        } else {
            sentencia.setBigDecimal(
                    4,
                    joya.getQuilates()
            );
        }

        sentencia.setBigDecimal(
                5,
                joya.getPrecio()
        );

        sentencia.setInt(
                6,
                joya.getStock()
        );

        if (joya.getCategoria() == null) {
            sentencia.setNull(7, Types.INTEGER);
        } else {
            sentencia.setInt(
                    7,
                    joya.getCategoria()
                            .getIdCategoria()
            );
        }
    }

    private void validarJoya(Joya joya) {

        if (joya == null) {
            throw new IllegalArgumentException(
                    "La joya es obligatoria"
            );
        }

        if (joya.getCategoria() != null &&
                joya.getCategoria()
                        .getIdCategoria() <= 0) {

            throw new IllegalArgumentException(
                    "La categoría debe existir " +
                            "en la base de datos"
            );
        }
    }

    private Joya convertirEnJoya(
            ResultSet resultado) throws SQLException {

        Categoria categoria = null;

        int idCategoria =
                resultado.getInt("id_categoria");

        if (!resultado.wasNull()) {
            categoria = new Categoria(
                    idCategoria,
                    resultado.getString(
                            "nombre_categoria"
                    )
            );
        }

        return new Joya(
                resultado.getInt("id_joya"),
                resultado.getString("nombre_joya"),
                resultado.getString("descripcion"),
                resultado.getString("material"),
                resultado.getBigDecimal("quilates"),
                resultado.getBigDecimal("precio"),
                resultado.getInt("stock"),
                categoria
        );
    }
}