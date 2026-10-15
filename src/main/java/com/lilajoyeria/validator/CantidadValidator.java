package com.lilajoyeria.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("cantidadValidator")
public class CantidadValidator implements Validator<Integer> {

    @Override
    public void validate(
            FacesContext context,
            UIComponent component,
            Integer value
    ) throws ValidatorException {

        // Los campos vacíos se controlan con required="true".
        if (value == null) {
            return;
        }

        if (value <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
        }

        Object stock = component.getAttributes().get("stockDisponible");

        if (stock instanceof Number) {
            int limite = ((Number) stock).intValue();

            if (value > limite) {
                mostrarError(
                        "La cantidad supera el inventario disponible. "
                                + "Máximo permitido: " + limite + "."
                );
            }
        }
    }

    private void mostrarError(String detalle) {
        throw new ValidatorException(
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Cantidad inválida",
                        detalle
                )
        );
    }
}