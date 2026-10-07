package com.lilajoyeria.dao;

import com.lilajoyeria.model.Usuario;
import jakarta.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UsuarioDAO extends JpaDAO {
    private static final String CONSULTA = "SELECT e FROM Usuario e";
    public UsuarioDAO() { super(); }
    public UsuarioDAO(EntityManagerFactory fabrica) { super(fabrica); }

    public List<Usuario> listar() throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " ORDER BY e.nombre", Usuario.class).getResultList());
    }
    public Usuario buscarPorId(int id) throws SQLException {
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.idUsuario = :id", Usuario.class)
                .setParameter("id", id).getResultStream().findFirst().orElse(null));
    }
    public int insertar(Usuario entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        if (entidad.getIdUsuario() != 0) throw new IllegalArgumentException("La entidad ya tiene identificador");
        return ejecutar(true, em -> { em.persist(entidad); em.flush(); return entidad.getIdUsuario(); });
    }
    public boolean actualizar(Usuario entidad) throws SQLException {
        Objects.requireNonNull(entidad, "La entidad es obligatoria");
        return ejecutar(true, em -> {
            if (em.find(Usuario.class, entidad.getIdUsuario()) == null) return false;
            em.merge(entidad);
            return true;
        });
    }
    public boolean eliminar(int id) throws SQLException {
        return ejecutar(true, em -> em.createQuery("DELETE FROM Usuario e WHERE e.idUsuario = :id")
                .setParameter("id", id).executeUpdate() > 0);
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("El correo es obligatorio");
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.email = :email", Usuario.class)
                .setParameter("email", email.trim()).getResultStream().findFirst().orElse(null));
    }
    public Usuario validarLogin(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) return null;
        return ejecutar(false, em -> em.createQuery(CONSULTA + " WHERE e.email = :email AND e.password = :password", Usuario.class)
                .setParameter("email", email.trim()).setParameter("password", password)
                .getResultStream().findFirst().orElse(null));
    }
}
