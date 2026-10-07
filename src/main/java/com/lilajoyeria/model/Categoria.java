package com.lilajoyeria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_categoria")
    private int idCategoria;
    @Column(nullable = false, length = 50)
    private String nombre;

    public Categoria() {
    }

    public Categoria(String nombre) {
        setNombre(nombre);
    }

    public Categoria(int idCategoria, String nombre) {
        setIdCategoria(idCategoria);
        setNombre(nombre);
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        if (idCategoria < 0) {
            throw new IllegalArgumentException(
                    "El identificador de la categoría no puede ser negativo"
            );
        }
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio"
            );
        }

        if (nombre.length() > 50) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría no puede superar 50 caracteres"
            );
        }

        this.nombre = nombre.trim();
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}