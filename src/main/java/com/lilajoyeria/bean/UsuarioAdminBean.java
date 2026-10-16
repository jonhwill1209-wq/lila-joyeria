package com.lilajoyeria.bean;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Tabla de administración de usuarios: filtrado por texto/rol, edición de
 * nombre, correo y rol, y eliminación. Solo ADMIN (AccesoFilter sobre /admin/*).
 */
@Named
@ViewScoped
public class UsuarioAdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AuthBean authBean;

    private List<Usuario> todos = new ArrayList<>();
    private List<Usuario> visibles = new ArrayList<>();

    // Filtros
    private String filtroTexto;
    private RolUsuario filtroRol;

    // Formulario de edición
    private boolean formularioVisible;
    private int idUsuario;
    private String nombre;
    private String correo;
    private RolUsuario rol;

    @PostConstruct
    public void iniciar() {
        cargar();
    }

    public void cargar() {
        try {
            todos = new UsuarioDAO().listar();
            filtrar();
        } catch (SQLException e) {
            FacesUtil.error("No fue posible cargar los usuarios.");
        }
    }

    public void filtrar() {
        String buscado = filtroTexto == null
                ? ""
                : filtroTexto.trim().toLowerCase(Locale.ROOT);

        visibles = todos.stream()
                .filter(u -> buscado.isEmpty()
                        || u.getNombre().toLowerCase(Locale.ROOT).contains(buscado)
                        || u.getEmail().toLowerCase(Locale.ROOT).contains(buscado))
                .filter(u -> filtroRol == null || u.getRol() == filtroRol)
                .collect(Collectors.toList());
    }

    public void limpiarFiltros() {
        filtroTexto = null;
        filtroRol = null;
        filtrar();
    }

    public void editar(Usuario usuario) {
        idUsuario = usuario.getIdUsuario();
        nombre = usuario.getNombre();
        correo = usuario.getEmail();
        rol = usuario.getRol();

        formularioVisible = true;
        FacesUtil.reiniciarEntradas("formEdicion");
    }

    public void cancelar() {
        formularioVisible = false;
        FacesUtil.reiniciarEntradas("formEdicion");
    }

    public void guardar() {
        if (esUsuarioActual(idUsuario) && rol != RolUsuario.ADMIN) {
            FacesUtil.error("No puedes quitarte a ti mismo el rol de administrador.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();

            // Se parte del registro actual para conservar la contraseña.
            Usuario usuario = dao.buscarPorId(idUsuario);

            if (usuario == null) {
                FacesUtil.error("El usuario ya no existe.");
                cargar();
                return;
            }

            usuario.setNombre(nombre);
            usuario.setEmail(correo);
            usuario.setRol(rol);

            dao.actualizar(usuario);
            authBean.refrescarUsuario(usuario);

            FacesUtil.info("Usuario actualizado correctamente.");
            formularioVisible = false;
            cargar();

        } catch (IllegalArgumentException e) {
            FacesUtil.error(e.getMessage());
        } catch (SQLException e) {
            FacesUtil.error("No fue posible guardar el usuario. "
                    + "Verifica que el correo no esté en uso.");
        }
    }

    public void eliminar(Usuario usuario) {
        if (esUsuarioActual(usuario.getIdUsuario())) {
            FacesUtil.error("No puedes eliminar tu propia cuenta.");
            return;
        }

        try {
            if (new UsuarioDAO().eliminar(usuario.getIdUsuario())) {
                FacesUtil.info("Usuario eliminado: " + usuario.getEmail() + ".");
            } else {
                FacesUtil.error("El usuario ya no existe.");
            }

            if (formularioVisible && idUsuario == usuario.getIdUsuario()) {
                cancelar();
            }
        } catch (Exception e) {
            // Típicamente: el usuario tiene pedidos (clave foránea).
            FacesUtil.error("No se puede eliminar a \"" + usuario.getEmail()
                    + "\": probablemente tiene pedidos asociados.");
        }

        cargar();
    }

    public boolean esUsuarioActual(int id) {
        return authBean.getUsuarioActual() != null
                && authBean.getUsuarioActual().getIdUsuario() == id;
    }

    public RolUsuario[] getRoles() {
        return RolUsuario.values();
    }

    public List<Usuario> getUsuarios() {
        return visibles;
    }

    public int getTotalUsuarios() {
        return todos.size();
    }

    public boolean isFormularioVisible() {
        return formularioVisible;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }

    public RolUsuario getFiltroRol() {
        return filtroRol;
    }

    public void setFiltroRol(RolUsuario filtroRol) {
        this.filtroRol = filtroRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
