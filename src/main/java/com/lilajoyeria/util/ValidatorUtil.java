package com.lilajoyeria.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class ValidatorUtil {

    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ValidatorUtil() {
    }

    public static void validarNoNulo(Object valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo + " es obligatorio."
            );
        }
    }

    public static void validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo + " es obligatorio."
            );
        }
    }

    public static void validarEmail(String email) {
        validarTextoObligatorio(email, "email");

        if (!PATRON_EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "El correo electrónico no tiene un formato válido."
            );
        }
    }

    public static void validarPrecioMayorQueCero(BigDecimal precio) {
        validarNoNulo(precio, "precio");

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }
    }
}