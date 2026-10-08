package com.lilajoyeria.dao;

import com.lilajoyeria.model.Joya;
import jakarta.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class JoyaDAO extends JpaDAO {
    private static final String CONSULTA = "SELECT e FROM Joya e LEFT JOIN FETCH e.categoria";
    public JoyaDAO() { super(); }
    public JoyaDAO(EntityManagerFactory fabrica) { super(fabrica); }

    public List<Joya> listar() throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " ORDER BY e.nombre", Joya.class).getResultList());
    }
    public Joya buscarPorId(int id) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.idJoya = :id", Joya.class)
                .setParameter("id", id).getResultStream().findFirst().orElse(null));
    }
    public int insertar(Joya entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        if (entidad.getIdJoya() != 0) throw new IllegalArgumentException("La entidad ya tiene identificador");
        return ejecutar(true, em -> { em.persist(entidad); em.flush(); return entidad.getIdJoya(); });
    }
    public boolean actualizar(Joya entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        return ejecutar(true, em -> {
            if (em.find(Joya.class, entidad.getIdJoya()) == null) return false;
            em.merge(entidad);
            return true;
        });
    }
    public boolean eliminar(int id) throws SQLException {
        return ejecutar(true, em -> em.createQuery("DELETE FROM Joya e WHERE e.idJoya = :id")
                .setParameter("id", id).executeUpdate() > 0);
    }

    public List<Joya> obtenerJoyasPorCategoria(int idCategoria) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.categoria.idCategoria = :id ORDER BY e.nombre", Joya.class)
                .setParameter("id", idCategoria).getResultList());
    }
}
