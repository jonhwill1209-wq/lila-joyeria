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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CatalogoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Joya> todas = new ArrayList<>();
    private List<Joya> joyas = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    // Filtros de la vista
    private String texto;
    private Integer idCategoria;
    private boolean soloDisponibles;

    @PostConstruct
    public void iniciar() {
        cargarJoyas();
    }

    public void cargarJoyas() {
        try {
            todas = new JoyaDAO().listar();
            categorias = new CategoriaDAO().listar();
            filtrar();
        } catch (SQLException e) {
            FacesUtil.error("No fue posible cargar el catálogo.");
        }
    }

    /** Aplica los filtros sobre las joyas ya cargadas. */
    public void filtrar() {
        String buscado = texto == null
                ? ""
                : texto.trim().toLowerCase(Locale.ROOT);

        joyas = todas.stream()
                .filter(j -> buscado.isEmpty()
                        || contiene(j.getNombre(), buscado)
                        || contiene(j.getDescripcion(), buscado)
                        || contiene(j.getMaterial(), buscado))
                .filter(j -> idCategoria == null
                        || (j.getCategoria() != null
                        && j.getCategoria().getIdCategoria() == idCategoria))
                .filter(j -> !soloDisponibles || j.getStock() > 0)
                .collect(Collectors.toList());
    }

    public void limpiar() {
        texto = null;
        idCategoria = null;
        soloDisponibles = false;
        filtrar();
    }

    private static boolean contiene(String valor, String buscado) {
        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(buscado);
    }

    /** Joyas que cumplen los filtros actuales. */
    public List<Joya> getJoyas() {
        return joyas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public boolean isSoloDisponibles() {
        return soloDisponibles;
    }

    public void setSoloDisponibles(boolean soloDisponibles) {
        this.soloDisponibles = soloDisponibles;
    }
}
