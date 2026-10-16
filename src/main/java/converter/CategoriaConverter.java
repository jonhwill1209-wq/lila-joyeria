package com.lilajoyeria.converter;

import com.lilajoyeria.bean.CatalogoBean;
import com.lilajoyeria.model.Categoria;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("categoriaConverter")
public class CategoriaConverter implements Converter<Categoria> {

    @Override
    public Categoria getAsObject(
            FacesContext context,
            UIComponent component,
            String value
    ) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        final int id;

        try {
            id = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw errorCategoria();
        }

        CatalogoBean catalogoBean =
                context.getApplication().evaluateExpressionGet(
                        context,
                        "#{catalogoBean}",
                        CatalogoBean.class
                );

        if (catalogoBean == null) {
            throw new ConverterException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No fue posible obtener las categorías del catálogo."
                    )
            );
        }

        for (Categoria categoria : catalogoBean.getCategorias()) {
            if (categoria.getIdCategoria() == id) {
                return categoria;
            }
        }

        throw errorCategoria();
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Categoria value
    ) {
        if (value == null) {
            return "";
        }

        return String.valueOf(value.getIdCategoria());
    }

    private ConverterException errorCategoria() {
        return new ConverterException(
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Categoría inválida",
                        "Selecciona una categoría disponible."
                )
        );
    }
}