package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.Joya;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class JoyaDAOTest {

    public static void main(String[] args) {

        CategoriaDAO categoriaDAO =
                new CategoriaDAO();

        JoyaDAO joyaDAO =
                new JoyaDAO();

        int idJoyaTemporal = 0;

        try {
            List<Categoria> categorias =
                    categoriaDAO.listar();

            comprobar(
                    !categorias.isEmpty(),
                    "Debe existir al menos una categoría"
            );

            Categoria categoria =
                    categorias.get(0);

            Joya joya = new Joya(
                    "Joya temporal DAO",
                    "Registro utilizado para la prueba",
                    "Oro",
                    new BigDecimal("18.00"),
                    new BigDecimal("125.50"),
                    3,
                    categoria
            );

            idJoyaTemporal =
                    joyaDAO.insertar(joya);

            comprobar(
                    idJoyaTemporal > 0,
                    "La joya debe recibir un ID"
            );

            System.out.println(
                    "CORRECTO: joya insertada con ID " +
                            idJoyaTemporal
            );

            Joya encontrada =
                    joyaDAO.buscarPorId(idJoyaTemporal);

            comprobar(
                    encontrada != null,
                    "La joya debe encontrarse por ID"
            );

            System.out.println(
                    "CORRECTO: búsqueda por ID"
            );

            List<Joya> joyasPorCategoria =
                    joyaDAO.obtenerJoyasPorCategoria(
                            categoria.getIdCategoria()
                    );

            comprobar(
                    contieneJoya(
                            joyasPorCategoria,
                            idJoyaTemporal
                    ),
                    "La joya debe aparecer en su categoría"
            );

            System.out.println(
                    "CORRECTO: filtro por categoría"
            );

            joya.setPrecio(
                    new BigDecimal("150.00")
            );

            joya.setStock(5);

            comprobar(
                    joyaDAO.actualizar(joya),
                    "La joya debe actualizarse"
            );

            Joya actualizada =
                    joyaDAO.buscarPorId(idJoyaTemporal);

            comprobar(
                    actualizada != null &&
                            actualizada.getPrecio().compareTo(
                                    new BigDecimal("150.00")
                            ) == 0 &&
                            actualizada.getStock() == 5,
                    "Los cambios deben guardarse"
            );

            System.out.println(
                    "CORRECTO: joya actualizada"
            );

            comprobar(
                    contieneJoya(
                            joyaDAO.listar(),
                            idJoyaTemporal
                    ),
                    "La joya debe aparecer en el listado"
            );

            System.out.println(
                    "CORRECTO: listado de joyas"
            );

            comprobar(
                    joyaDAO.eliminar(idJoyaTemporal),
                    "La joya debe eliminarse"
            );

            comprobar(
                    joyaDAO.buscarPorId(
                            idJoyaTemporal
                    ) == null,
                    "La joya ya no debe existir"
            );

            System.out.println(
                    "CORRECTO: joya temporal eliminada"
            );

            System.out.println(
                    "Todas las pruebas de JoyaDAO " +
                            "finalizaron correctamente"
            );

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "Falló la prueba de JoyaDAO",
                    exception
            );

        } finally {

            eliminarRegistroTemporal(
                    joyaDAO,
                    idJoyaTemporal
            );
        }
    }

    private static boolean contieneJoya(
            List<Joya> joyas,
            int idJoya) {

        for (Joya joya : joyas) {
            if (joya.getIdJoya() == idJoya) {
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
            JoyaDAO joyaDAO,
            int idJoya) {

        if (idJoya <= 0) {
            return;
        }

        try {
            if (joyaDAO.buscarPorId(idJoya) != null) {
                joyaDAO.eliminar(idJoya);

                System.out.println(
                        "LIMPIEZA: se eliminó la joya temporal"
                );
            }

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "No se pudo eliminar la joya temporal",
                    exception
            );
        }
    }
}