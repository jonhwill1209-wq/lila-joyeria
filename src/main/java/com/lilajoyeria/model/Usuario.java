package com.lilajoyeria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_usuario")
    private int idUsuario;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Enumerated(EnumType.STRING) @Column(length = 7)
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