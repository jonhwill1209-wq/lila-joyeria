package com.lilajoyeria.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator<String> {

    @Override
    public void validate(
            FacesContext context,
            UIComponent component,
            String value
    ) throws ValidatorException {

        // Los campos vacíos se controlan con required="true".
        if (value == null || value.isEmpty()) {
            return;
        }

        boolean tieneMayuscula = value.chars()
                .anyMatch(Character::isUpperCase);

        boolean tieneMinuscula = value.chars()
                .anyMatch(Character::isLowerCase);

        boolean tieneNumero = value.chars()
                .anyMatch(Character::isDigit);

        if (value.length() < 8
                || !tieneMayuscula
                || !tieneMinuscula
                || !tieneNumero) {

            throw new ValidatorException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Contraseña inválida",
                            "La contraseña debe tener al menos 8 caracteres, "
                                    + "una mayúscula, una minúscula y un número."
                    )
            );
        }
    }
}