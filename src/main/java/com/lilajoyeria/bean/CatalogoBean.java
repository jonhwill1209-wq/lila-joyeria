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
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CatalogoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(CatalogoBean.class.getName());

    private transient JoyaDAO joyaDAO;
    private transient CategoriaDAO categoriaDAO;

    private List<Joya> joyas = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    private Categoria categoriaSeleccionada;

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
            if (categoriaSeleccionada == null) {
                joyas = obtenerJoyaDAO().listar();
            } else {
                joyas = obtenerJoyaDAO().obtenerJoyasPorCategoria(
                        categoriaSeleccionada.getIdCategoria()
                );
            }

            LOGGER.log(
                    Level.INFO,
                    "Catálogo cargado: {0} productos.",
                    joyas.size()
            );
        } catch (Exception e) {
            joyas = new ArrayList<>();
            LOGGER.log(Level.SEVERE, "Error al cargar el catálogo.", e);
            mostrarError("No fue posible cargar el catálogo.");
        }
    }

    public void filtrarPorCategoria() {
        cargarJoyas();
    }

    public List<Joya> getJoyas() {
        return joyas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public Categoria getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
        this.categoriaSeleccionada = categoriaSeleccionada;
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