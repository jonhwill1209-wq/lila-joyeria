package com.lilajoyeria.bean;

import com.lilajoyeria.dao.UsuarioDAO;
import com.lilajoyeria.model.RolUsuario;
import com.lilajoyeria.model.Usuario;
import com.lilajoyeria.util.FacesUtil;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Registro de nuevos clientes. El rol nunca viene del formulario: toda
 * cuenta creada desde aquí es CLIENTE.
 */
@Named
@ViewScoped
public class RegistroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String correo;
    private String password;
    private String confirmarPassword;

    public String registrar() {
        if (password == null || !password.equals(confirmarPassword)) {
            FacesUtil.error("Las contraseñas no coinciden.");
            return null;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            if (usuarioDAO.buscarPorEmail(correo) != null) {
                FacesUtil.error("Ya existe una cuenta con ese correo.");
                return null;
            }

            Usuario nuevo = new Usuario(
                    nombre, correo, password, RolUsuario.CLIENTE
            );
            usuarioDAO.insertar(nuevo);

            FacesUtil.info("Registro completado. Ya puedes iniciar sesión.");
            FacesUtil.mantenerMensajes();
            return "login?faces-redirect=true";

        } catch (IllegalArgumentException e) {
            // Mensajes de validación del modelo (nombre, correo, contraseña).
            FacesUtil.error(e.getMessage());
        } catch (SQLException e) {
            FacesUtil.error("No fue posible completar el registro.");
        }

        return null;
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
