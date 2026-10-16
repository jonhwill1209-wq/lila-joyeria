package com.lilajoyeria.converter;

import com.lilajoyeria.bean.DetalleJoyaBean;
import com.lilajoyeria.model.Joya;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("joyaConverter")
public class JoyaConverter implements Converter<Joya> {

    @Override
    public Joya getAsObject(
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
            throw errorJoya();
        }

        DetalleJoyaBean bean =
                context.getApplication().evaluateExpressionGet(
                        context,
                        "#{detalleJoyaBean}",
                        DetalleJoyaBean.class
                );

        if (bean == null) {
            throw errorJoya();
        }

        for (Joya joya : bean.getJoyas()) {
            if (joya.getIdJoya() == id) {
                return joya;
            }
        }

        throw errorJoya();
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Joya value
    ) {
        if (value == null) {
            return "";
        }

        return String.valueOf(value.getIdJoya());
    }

    private ConverterException errorJoya() {
        return new ConverterException(
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Joya inválida",
                        "Selecciona una joya disponible."
                )
        );
    }
}