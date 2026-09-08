package com.lilajoyeria.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class ConexionBDTest {

    public static void main(String[] args) {

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            DatabaseMetaData datos =
                    conexion.getMetaData();

            System.out.println(
                    "CORRECTO: conexión establecida"
            );

            System.out.println(
                    "Base de datos: " +
                            conexion.getCatalog()
            );

            System.out.println(
                    "Servidor: " +
                            datos.getDatabaseProductName()
            );

            System.out.println(
                    "Versión: " +
                            datos.getDatabaseProductVersion()
            );

        } catch (SQLException exception) {

            System.err.println(
                    "ERROR: no fue posible conectarse a MySQL"
            );

            exception.printStackTrace();
        }
    }
}