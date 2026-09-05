package com.lilajoyeria.model;

import java.math.BigDecimal;

public class Joya {

    private int idJoya;
    private String nombre;
    private String descripcion;
    private String material;
    private BigDecimal quilates;
    private BigDecimal precio;
    private int stock;
    private Categoria categoria;

    public Joya() {
    }

    public Joya(String nombre, String descripcion, String material,
                BigDecimal quilates, BigDecimal precio, int stock,
                Categoria categoria) {
        setNombre(nombre);
        setDescripcion(descripcion);
        setMaterial(material);
        setQuilates(quilates);
        setPrecio(precio);
        setStock(stock);
        setCategoria(categoria);
    }

    public Joya(int idJoya, String nombre, String descripcion,
                String material, BigDecimal quilates, BigDecimal precio,
                int stock, Categoria categoria) {
        setIdJoya(idJoya);
        setNombre(nombre);
        setDescripcion(descripcion);
        setMaterial(material);
        setQuilates(quilates);
        setPrecio(precio);
        setStock(stock);
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
                ", categoria=" + categoria +
                '}';
    }
}