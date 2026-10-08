package com.lilajoyeria.dao;

import com.lilajoyeria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class JpaDAO {
    private final Supplier<EntityManager> proveedor;
    protected JpaDAO() { proveedor = JPAUtil::crearEntityManager; }
    protected JpaDAO(EntityManagerFactory fabrica) { proveedor = fabrica::createEntityManager; }

    protected <T> T ejecutar(boolean escritura, Function<EntityManager, T> operacion) throws SQLException {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = proveedor.get();
            if (escritura) { tx = em.getTransaction(); tx.begin(); }
            T resultado = operacion.apply(em);
            if (escritura) tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                try { tx.rollback(); } catch (RuntimeException rollback) { e.addSuppressed(rollback); }
            }
            // Conserva el contrato de los servlets existentes durante la migración.
            if (e instanceof PersistenceException || e instanceof IllegalStateException) {
                throw new SQLException("Falló la operación de persistencia", e);
            }
            throw e;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
}
