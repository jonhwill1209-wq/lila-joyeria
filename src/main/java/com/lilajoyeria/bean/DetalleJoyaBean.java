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
public class DetalleJoyaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(DetalleJoyaBean.class.getName());

    private List<Categoria> categorias = new ArrayList<>();
    private List<Joya> joyas = new ArrayList<>();

    private Integer idCategoriaSeleccionada;
    private Joya joyaSeleccionada;

    @PostConstruct
    public void iniciar() {
        try {
            categorias = new CategoriaDAO().listar();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar categorías.", e);
            mostrarError("No fue posible cargar las categorías.");
        }
    }

    public void cargarJoyasPorCategoria() {
        joyaSeleccionada = null;
        joyas = new ArrayList<>();

        if (idCategoriaSeleccionada == null) {
            return;
        }

        try {
            joyas = new JoyaDAO().obtenerJoyasPorCategoria(
                    idCategoriaSeleccionada
            );
        } catch (Exception e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Error al cargar las joyas de la categoría.",
                    e
            );

            mostrarError("No fue posible cargar las joyas.");
        }
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Joya> getJoyas() {
        return joyas;
    }

    public Integer getIdCategoriaSeleccionada() {
        return idCategoriaSeleccionada;
    }

    public void setIdCategoriaSeleccionada(Integer idCategoriaSeleccionada) {
        this.idCategoriaSeleccionada = idCategoriaSeleccionada;
    }

    public Joya getJoyaSeleccionada() {
        return joyaSeleccionada;
    }

    public void setJoyaSeleccionada(Joya joyaSeleccionada) {
        this.joyaSeleccionada = joyaSeleccionada;
    }

    private void mostrarError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error",
                        mensaje
                )
        );
    }
}