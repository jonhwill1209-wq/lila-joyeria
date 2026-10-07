package com.lilajoyeria.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** Una fábrica por aplicación; un EntityManager por operación, nunca compartido. */
public final class JPAUtil {
    private static EntityManagerFactory fabrica;
    private JPAUtil() { }

    public static synchronized EntityManager crearEntityManager() {
        if (fabrica == null || !fabrica.isOpen()) {
            Properties config = new Properties();
            try (InputStream in = JPAUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (in != null) config.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer database.properties", e);
            }
            Map<String, Object> propiedades = new HashMap<>();
            propiedades.put("jakarta.persistence.jdbc.url", valor(config, "DB_URL", "db.url", null));
            propiedades.put("jakarta.persistence.jdbc.user", valor(config, "DB_USER", "db.user", null));
            propiedades.put("jakarta.persistence.jdbc.password", valor(config, "DB_PASSWORD", "db.password", ""));
            fabrica = Persistence.createEntityManagerFactory("lilaJoyeriaPU", propiedades);
        }
        return fabrica.createEntityManager();
    }

    private static String valor(Properties config, String entorno, String clave, String defecto) {
        String valor = System.getenv(entorno);
        if (valor == null) valor = config.getProperty(clave, defecto);
        if (valor == null) throw new IllegalStateException("Falta configurar " + clave + " o " + entorno);
        return valor;
    }

    public static synchronized void cerrar() {
        if (fabrica != null && fabrica.isOpen()) fabrica.close();
        fabrica = null;
    }
}
