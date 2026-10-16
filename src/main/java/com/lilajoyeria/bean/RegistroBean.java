package com.lilajoyeria.bean;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class RegistroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(RegistroBean.class.getName());

    private String nombre;
    private String correo;
    private String password;
    private String confirmarPassword;

    public String registrar() {
        FacesContext contexto = FacesContext.getCurrentInstance();

        if (password == null || !password.equals(confirmarPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            return null;
        }

        if (nombre == null || nombre.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || password.isEmpty()) {
            mostrarError("Completa todos los campos obligatorios.");
            return null;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            if (usuarioDAO.buscarPorEmail(correo.trim()) != null) {
                mostrarError("Ya existe una cuenta con ese correo.");
                return null;
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setEmail(correo.trim());
            usuario.setPassword(password);

            // El rol se establece en el servidor.
            usuario.setRol(RolUsuario.CLIENTE);

            int idGenerado = usuarioDAO.insertar(usuario);

            if (idGenerado <= 0) {
                mostrarError("No fue posible crear la cuenta.");
                return null;
            }

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario.", e);
            mostrarError(
                    "No fue posible completar el registro. "
                            + "Inténtalo nuevamente."
            );
            return null;

        } catch (Exception e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Error inesperado al registrar usuario.",
                    e
            );

            mostrarError(
                    "No fue posible crear la cuenta. "
                            + "Inténtalo nuevamente."
            );
            return null;
        }

        password = null;
        confirmarPassword = null;

        contexto.getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        contexto.addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Registro completado",
                        "Cuenta creada correctamente. Ya puedes iniciar sesión."
                )
        );

        return "/login.xhtml?faces-redirect=true";
    }

    private void mostrarError(String mensaje) {
        FacesContext contexto = FacesContext.getCurrentInstance();

        contexto.validationFailed();

        contexto.addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error",
                        mensaje
                )
        );
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}