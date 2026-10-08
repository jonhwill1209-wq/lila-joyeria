package com.lilajoyeria.bean;

import com.lilajoyeria.dao.JoyaDAO;
import com.lilajoyeria.model.Joya;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CatalogoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final JoyaDAO joyaDAO = new JoyaDAO();

    private List<Joya> joyas;

    @PostConstruct
    public void iniciar() {
        cargarJoyas();
    }

    public void cargarJoyas() {
        try {
            joyas = joyaDAO.listar();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No fue posible cargar el catálogo."
                    )
            );
        }
    }

    public List<Joya> getJoyas() {
        return joyas;
    }
}