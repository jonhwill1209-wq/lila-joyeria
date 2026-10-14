package com.lilajoyeria.bean;

import com.lilajoyeria.dao.CategoriaDAO;
import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.Joya;
import com.lilajoyeria.util.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Tabla de administración de joyas: filtrado, alta/edición y eliminación.
 * Solo accesible para ADMIN (ver AccesoFilter sobre /admin/*).
 */
@Named
@ViewScoped
public class JoyaAdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Una joya con este stock o menos se marca como "stock bajo". */
    public static final int UMBRAL_STOCK_BAJO = 3;

    private List<Joya> todas = new ArrayList<>();
    private List<Joya> visibles = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    // Filtros
    private String filtroTexto;
    private Integer filtroCategoria;
    private boolean soloStockBajo;

    // Formulario de alta/edición
    private boolean formularioVisible;
    private int idJoya;
    private String nombre;
    private String descripcion;
    private String material;
    private BigDecimal quilates;
    private BigDecimal precio;
    private Integer stock;
    private String imagen;
    private Integer idCategoria;

    @PostConstruct
    public void iniciar() {
        cargar();
    }

    public void cargar() {
        try {
            todas = new JoyaDAO().listar();
            categorias = new CategoriaDAO().listar();
            filtrar();
        } catch (SQLException e) {
            FacesUtil.error("No fue posible cargar las joyas.");
        }
    }

    // ---------------------------------------------------------------- filtros

    public void filtrar() {
        String buscado = filtroTexto == null
                ? ""
                : filtroTexto.trim().toLowerCase(Locale.ROOT);

        visibles = todas.stream()
                .filter(j -> buscado.isEmpty()
                        || contiene(j.getNombre(), buscado)
                        || contiene(j.getMaterial(), buscado)
                        || contiene(j.getDescripcion(), buscado))
                .filter(j -> filtroCategoria == null
                        || (j.getCategoria() != null
                        && j.getCategoria().getIdCategoria() == filtroCategoria))
                .filter(j -> !soloStockBajo
                        || j.getStock() <= UMBRAL_STOCK_BAJO)
                .collect(Collectors.toList());
    }

    public void limpiarFiltros() {
        filtroTexto = null;
        filtroCategoria = null;
        soloStockBajo = false;
        filtrar();
    }

    private static boolean contiene(String valor, String buscado) {
        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(buscado);
    }

    // ------------------------------------------------------------ edición/alta

    public void nueva() {
        vaciarFormulario();
        formularioVisible = true;
        FacesUtil.reiniciarEntradas("formEdicion");
    }

    public void editar(Joya joya) {
        idJoya = joya.getIdJoya();
        nombre = joya.getNombre();
        descripcion = joya.getDescripcion();
        material = joya.getMaterial();
        quilates = joya.getQuilates();
        precio = joya.getPrecio();
        stock = joya.getStock();
        imagen = joya.getImagen();
        idCategoria = joya.getCategoria() == null
                ? null
                : joya.getCategoria().getIdCategoria();

        formularioVisible = true;
        FacesUtil.reiniciarEntradas("formEdicion");
    }

    public void cancelar() {
        formularioVisible = false;
        vaciarFormulario();
        FacesUtil.reiniciarEntradas("formEdicion");
    }

    public void guardar() {
        try {
            Categoria categoria = idCategoria == null
                    ? null
                    : new CategoriaDAO().buscarPorId(idCategoria);

            Joya joya = new Joya(
                    nombre, descripcion, material, quilates, precio,
                    stock == null ? 0 : stock, imagen, categoria
            );

            JoyaDAO dao = new JoyaDAO();

            if (idJoya == 0) {
                dao.insertar(joya);
                FacesUtil.info("Joya creada correctamente.");
            } else {
                joya.setIdJoya(idJoya);

                if (!dao.actualizar(joya)) {
                    FacesUtil.error("La joya ya no existe.");
                    cargar();
                    return;
                }
                FacesUtil.info("Joya actualizada correctamente.");
            }

            formularioVisible = false;
            vaciarFormulario();
            cargar();

        } catch (IllegalArgumentException e) {
            // Validaciones del modelo Joya (precio negativo, nombre vacío...).
            FacesUtil.error(e.getMessage());
        } catch (SQLException e) {
            FacesUtil.error("No fue posible guardar la joya.");
        }
    }

    // ------------------------------------------------------------- eliminación

    public void eliminar(Joya joya) {
        try {
            if (new JoyaDAO().eliminar(joya.getIdJoya())) {
                FacesUtil.info("Joya eliminada: " + joya.getNombre() + ".");
            } else {
                FacesUtil.error("La joya ya no existe.");
            }

            if (formularioVisible && idJoya == joya.getIdJoya()) {
                cancelar();
            }
        } catch (Exception e) {
            // Típicamente: la joya aparece en detalles_pedido (clave foránea).
            FacesUtil.error("No se puede eliminar \"" + joya.getNombre()
                    + "\": probablemente tiene pedidos asociados.");
        }

        cargar();
    }

    private void vaciarFormulario() {
        idJoya = 0;
        nombre = null;
        descripcion = null;
        material = null;
        quilates = null;
        precio = null;
        stock = null;
        imagen = null;
        idCategoria = null;
    }

    public boolean isStockBajo(Joya joya) {
        return joya.getStock() <= UMBRAL_STOCK_BAJO;
    }

    // ------------------------------------------------------- getters/setters

    public List<Joya> getJoyas() {
        return visibles;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public int getTotalJoyas() {
        return todas.size();
    }

    public boolean isFormularioVisible() {
        return formularioVisible;
    }

    public boolean isNuevaJoya() {
        return idJoya == 0;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }

    public Integer getFiltroCategoria() {
        return filtroCategoria;
    }

    public void setFiltroCategoria(Integer filtroCategoria) {
        this.filtroCategoria = filtroCategoria;
    }

    public boolean isSoloStockBajo() {
        return soloStockBajo;
    }

    public void setSoloStockBajo(boolean soloStockBajo) {
        this.soloStockBajo = soloStockBajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
}
