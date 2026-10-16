package com.lilajoyeria.bean;

import com.lilajoyeria.dao.CategoriaDAO;
import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.Categoria;
import com.lilajoyeria.model.Joya;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CatalogoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(CatalogoBean.class.getName());

    private transient JoyaDAO joyaDAO;
    private transient CategoriaDAO categoriaDAO;

    private List<Joya> todas = new ArrayList<>();
    private List<Joya> joyas = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    private String texto;
    private Integer idCategoria;
    private boolean soloDisponibles;

    @PostConstruct
    public void iniciar() {
        cargarCategorias();
        cargarJoyas();
    }

    private JoyaDAO obtenerJoyaDAO() {
        if (joyaDAO == null) {
            joyaDAO = new JoyaDAO();
        }
        return joyaDAO;
    }

    private CategoriaDAO obtenerCategoriaDAO() {
        if (categoriaDAO == null) {
            categoriaDAO = new CategoriaDAO();
        }
        return categoriaDAO;
    }

    public void cargarCategorias() {
        try {
            categorias = obtenerCategoriaDAO().listar();
        } catch (Exception e) {
            categorias = new ArrayList<>();
            LOGGER.log(Level.SEVERE, "Error al cargar las categorías.", e);
            mostrarError("No fue posible cargar las categorías.");
        }
    }

    public void cargarJoyas() {
        try {
            todas = obtenerJoyaDAO().listar();
            filtrar();

            LOGGER.log(
                    Level.INFO,
                    "Catálogo cargado: {0} productos.",
                    todas.size()
            );
        } catch (Exception e) {
            todas = new ArrayList<>();
            joyas = new ArrayList<>();
            LOGGER.log(Level.SEVERE, "Error al cargar el catálogo.", e);
            mostrarError("No fue posible cargar el catálogo.");
        }
    }

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
                        && j.getCategoria().getIdCategoria()
                        == idCategoria.intValue()))
                .filter(j -> !soloDisponibles || j.getStock() > 0)
                .collect(Collectors.toList());
    }

    public void filtrarPorCategoria() {
        filtrar();
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

    /*
     * Permite usar CategoriaConverter en los formularios.
     * Ambos tipos de selección utilizan el mismo idCategoria.
     */
    public Categoria getCategoriaSeleccionada() {
        if (idCategoria == null) {
            return null;
        }

        for (Categoria categoria : categorias) {
            if (categoria.getIdCategoria() == idCategoria.intValue()) {
                return categoria;
            }
        }

        return null;
    }

    public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
        idCategoria = categoriaSeleccionada == null
                ? null
                : categoriaSeleccionada.getIdCategoria();
    }

    private void mostrarError(String mensaje) {
        FacesContext contexto = FacesContext.getCurrentInstance();

        if (contexto != null) {
            contexto.addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            mensaje
                    )
            );
        }
    }
}