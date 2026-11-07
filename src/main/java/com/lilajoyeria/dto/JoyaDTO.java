package com.lilajoyeria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Datos que el cliente envía para crear (POST) o actualizar (PUT) una joya.
 * nombre, material, precio y stock son obligatorios (la BD los exige NOT NULL);
 * descripcion, quilates e imagen son opcionales.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoyaDTO {

    private String nombre;
    private String material;
    private String descripcion;
    private BigDecimal quilates;
    private BigDecimal precio;
    private Integer stock;
    private String imagen;
    private int idCategoria;

    public JoyaDTO() {
    }

    public JoyaDTO(String nombre, BigDecimal precio, int stock, int idCategoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public JoyaDTO(String nombre, String material, BigDecimal precio, int stock, int idCategoria) {
        this(nombre, precio, stock, idCategoria);
        this.material = material;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getQuilates() {
        return quilates;
    }

    public void setQuilates(BigDecimal quilates) {
        this.quilates = quilates;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
}
