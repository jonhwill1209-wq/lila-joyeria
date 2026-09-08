package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaDAOTest {

    public static void main(String[] args) {

        CategoriaDAO categoriaDAO =
                new CategoriaDAO();

        try {
            List<Categoria> categorias =
                    categoriaDAO.listar();

            if (categorias.isEmpty()) {
                throw new AssertionError(
                        "No se encontraron categorías"
                );
            }

            System.out.println(
                    "CORRECTO: categorías encontradas"
            );

            for (Categoria categoria : categorias) {
                System.out.println(
                        categoria.getIdCategoria() +
                                " - " +
                                categoria.getNombre()
                );
            }

            Categoria primeraCategoria =
                    categorias.get(0);

            Categoria encontrada =
                    categoriaDAO.buscarPorId(
                            primeraCategoria
                                    .getIdCategoria()
                    );

            if (encontrada == null) {
                throw new AssertionError(
                        "No se encontró la categoría por ID"
                );
            }

            System.out.println(
                    "CORRECTO: búsqueda por ID"
            );

            System.out.println(
                    encontrada.getIdCategoria() +
                            " - " +
                            encontrada.getNombre()
            );

        } catch (SQLException exception) {

            System.err.println(
                    "ERROR: falló la prueba de CategoriaDAO"
            );

            exception.printStackTrace();
            System.exit(1);
        }
    }
}