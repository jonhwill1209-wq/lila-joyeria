package com.lilajoyeria.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.regex.Pattern;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator<String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@" +
                    "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+" +
                    "[A-Za-z]{2,63}$"
    );

    @Override
    public void validate(
            FacesContext context,
            UIComponent component,
            String value
    ) throws ValidatorException {

        // Los campos vacíos se controlan con required="true".
        if (value == null || value.isBlank()) {
            return;
        }

        if (value.length() > 254
                || !EMAIL_PATTERN.matcher(value).matches()) {

            FacesMessage mensaje = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Correo inválido",
                    "Ingresa un correo válido, por ejemplo: nombre@gmail.com."
            );

            throw new ValidatorException(mensaje);
        }
    }
}