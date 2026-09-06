package com.lilajoyeria.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION =
            "database.properties";

    private static final Properties PROPIEDADES =
            new Properties();

    static {
        cargarConfiguracion();
    }

    private ConexionBD() {
    }

    private static void cargarConfiguracion() {
        try (InputStream entrada = ConexionBD.class
                .getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIGURACION)) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontró database.properties"
                );
            }

            PROPIEDADES.load(entrada);

            String driver = obtenerPropiedadObligatoria(
                    "db.driver"
            );

            Class.forName(driver);

        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static Connection obtenerConexion()
            throws SQLException {

        String url = obtenerConfiguracion(
                "DB_URL",
                "db.url"
        );

        String usuario = obtenerConfiguracion(
                "DB_USER",
                "db.user"
        );

        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            password = PROPIEDADES.getProperty(
                    "db.password",
                    ""
            );
        }

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }

    private static String obtenerConfiguracion(
            String variableEntorno,
            String propiedad) {

        String valor = System.getenv(variableEntorno);

        if (valor != null && !valor.trim().isEmpty()) {
            return valor.trim();
        }

        return obtenerPropiedadObligatoria(propiedad);
    }

    private static String obtenerPropiedadObligatoria(
            String propiedad) {

        String valor = PROPIEDADES.getProperty(propiedad);

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Falta configurar la propiedad: " + propiedad
            );
        }

        return valor.trim();
    }
}