package com.lilajoyeria.bean;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Atributo de sesión que lee AccesoFilter para proteger las URL por rol. */
    public static final String ATRIBUTO_ROL = "rolUsuario";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private String correo;
    private String password;
    private Usuario usuarioActual;

    public String iniciarSesion() {
        try {
            usuarioActual = usuarioDAO.validarLogin(correo, password);
            password = null;

            if (usuarioActual != null) {
                publicarRolEnSesion();

                return usuarioActual.getRol() == RolUsuario.ADMIN
                        ? "/admin/joyas?faces-redirect=true"
                        : "catalogo?faces-redirect=true";
            }

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Correo o contraseña incorrectos."
                    )
            );
        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No fue posible iniciar sesión."
                    )
            );
        }

        return null;
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        usuarioActual = null;
        return "login?faces-redirect=true";
    }

    public boolean isAutenticado() {
        return usuarioActual != null;
    }

    public boolean isAdmin() {
        return usuarioActual != null
                && usuarioActual.getRol() == RolUsuario.ADMIN;
    }

    public boolean isCliente() {
        return usuarioActual != null
                && usuarioActual.getRol() == RolUsuario.CLIENTE;
    }

    /** Actualiza la copia de sesión si el administrador edita su propio perfil. */
    public void refrescarUsuario(Usuario usuario) {
        if (usuarioActual != null && usuario != null
                && usuarioActual.getIdUsuario() == usuario.getIdUsuario()) {
            usuarioActual = usuario;
        }
    }

    private void publicarRolEnSesion() {
        ExternalContext externo =
                FacesContext.getCurrentInstance().getExternalContext();

        externo.getSessionMap().put(ATRIBUTO_ROL, usuarioActual.getRol());
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}