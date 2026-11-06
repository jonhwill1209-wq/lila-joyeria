package com.lilajoyeria.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ValidatorUtilTest {

    @Test
    void aceptaDatosValidos() {
        assertDoesNotThrow(() -> {
            ValidatorUtil.validarEmail("norma@lilajoyeria.com");
            ValidatorUtil.validarPrecioMayorQueCero(
                    new BigDecimal("25.50")
            );
            ValidatorUtil.validarTextoObligatorio("Anillo", "nombre");
        });
    }

    @Test
    void rechazaCorreoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarEmail("correo-invalido")
        );
    }

    @Test
    void rechazaPrecioCeroONegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarPrecioMayorQueCero(BigDecimal.ZERO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarPrecioMayorQueCero(
                        new BigDecimal("-10")
                )
        );
    }

    @Test
    void rechazaTextoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarTextoObligatorio("", "nombre")
        );
    }
}