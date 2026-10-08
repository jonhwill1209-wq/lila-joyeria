package com.lilajoyeria.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "joyas")
public class Joya implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_joya")
    private int idJoya;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @Column(nullable = false, length = 50)
    private String material;
    @Column(precision = 4, scale = 2)
    private BigDecimal quilates;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(nullable = false)
    private int stock;
    @Column(length = 150)
    private String imagen;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    public Joya() {
    }

    public Joya(String nombre, String descripcion, String material,
                BigDecimal quilates, BigDecimal precio, int stock,
                String imagen, Categoria categoria) {
        setNombre(nombre);
        setDescripcion(descripcion);
        setMaterial(material);
        setQuilates(quilates);
        setPrecio(precio);
        setStock(stock);
        setImagen(imagen);
        setCategoria(categoria);
    }

    public Joya(int idJoya, String nombre, String descripcion,
                String material, BigDecimal quilates, BigDecimal precio,
                int stock, String imagen, Categoria categoria) {
        setIdJoya(idJoya);
        setNombre(nombre);
        setDescripcion(descripcion);
        setMaterial(material);
        setQuilates(quilates);
        setPrecio(precio);
        setStock(stock);
        setImagen(imagen);
        setCategoria(categoria);
    }

    public int getIdJoya() {
        return idJoya;
    }

    public void setIdJoya(int idJoya) {
        if (idJoya < 0) {
            throw new IllegalArgumentException(
                    "El identificador de la joya no puede ser negativo"
            );
        }
        this.idJoya = idJoya;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la joya es obligatorio"
            );
        }

        if (nombre.length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar 100 caracteres"
            );
        }

        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null
                ? null
                : descripcion.trim();
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El material de la joya es obligatorio"
            );
        }

        if (material.length() > 50) {
            throw new IllegalArgumentException(
                    "El material no puede superar 50 caracteres"
            );
        }

        this.material = material.trim();
    }

    public BigDecimal getQuilates() {
        return quilates;
    }

    public void setQuilates(BigDecimal quilates) {
        if (quilates != null &&
                quilates.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Los quilates no pueden ser negativos"
            );
        }
        this.quilates = quilates;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        if (precio == null ||
                precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio es obligatorio y no puede ser negativo"
            );
        }
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = (imagen == null || imagen.trim().isEmpty())
                ? "sin-imagen.jpg"
                : imagen.trim();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Joya{" +
                "idJoya=" + idJoya +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", material='" + material + '\'' +
                ", quilates=" + quilates +
                ", precio=" + precio +
                ", stock=" + stock +
                ", imagen='" + imagen + '\'' +
                ", categoria=" + categoria +
                '}';
    }
}