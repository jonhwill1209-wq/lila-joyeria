package com.lilajoyeria.dao;

import com.lilajoyeria.model.Categoria;
import jakarta.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CategoriaDAO extends JpaDAO {
    private static final String CONSULTA = "SELECT e FROM Categoria e";
    public CategoriaDAO() { super(); }
    public CategoriaDAO(EntityManagerFactory fabrica) { super(fabrica); }

    public List<Categoria> listar() throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " ORDER BY e.nombre", Categoria.class).getResultList());
    }
    public Categoria buscarPorId(int id) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.idCategoria = :id", Categoria.class)
                .setParameter("id", id).getResultStream().findFirst().orElse(null));
    }
    public int insertar(Categoria entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        if (entidad.getIdCategoria() != 0) throw new IllegalArgumentException("La entidad ya tiene identificador");
        return ejecutar(true, em -> { em.persist(entidad); em.flush(); return entidad.getIdCategoria(); });
    }
    public boolean actualizar(Categoria entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        return ejecutar(true, em -> {
            if (em.find(Categoria.class, entidad.getIdCategoria()) == null) return false;
            em.merge(entidad);
            return true;
        });
    }
    public boolean eliminar(int id) throws SQLException {
        return ejecutar(true, em -> em.createQuery("DELETE FROM Categoria e WHERE e.idCategoria = :id")
                .setParameter("id", id).executeUpdate() > 0);
    }
}
