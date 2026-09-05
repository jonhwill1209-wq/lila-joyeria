package com.lilajoyeria.model;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String email;
    private String password;
    private RolUsuario rol = RolUsuario.CLIENTE;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String password,
                   RolUsuario rol) {
        setNombre(nombre);
        setEmail(email);
        setPassword(password);
        setRol(rol);
    }

    public Usuario(int idUsuario, String nombre, String email,
                   String password, RolUsuario rol) {
        setIdUsuario(idUsuario);
        setNombre(nombre);
        setEmail(email);
        setPassword(password);
        setRol(rol);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        if (idUsuario < 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario no puede ser negativo"
            );
        }
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del usuario es obligatorio"
            );
        }

        if (nombre.length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar 100 caracteres"
            );
        }

        this.nombre = nombre.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El correo electrónico es obligatorio"
            );
        }

        if (email.length() > 100 || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido"
            );
        }

        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }

        if (password.length() > 255) {
            throw new IllegalArgumentException(
                    "La contraseña no puede superar 255 caracteres"
            );
        }

        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        if (rol == null) {
            throw new IllegalArgumentException(
                    "El rol del usuario es obligatorio"
            );
        }
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
}