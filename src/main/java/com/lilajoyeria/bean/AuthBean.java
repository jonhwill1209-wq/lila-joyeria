package com.lilajoyeria.bean;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private String correo;
    private String password;
    private Usuario usuarioActual;

    public String iniciarSesion() {
        try {
            usuarioActual = usuarioDAO.validarLogin(correo, password);
            password = null;

            if (usuarioActual != null) {
                return "catalogo?faces-redirect=true";
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