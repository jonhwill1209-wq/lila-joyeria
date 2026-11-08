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
    void aceptaYRechazaLimitesDePrecio() {
        assertDoesNotThrow(() ->
                ValidatorUtil.validarPrecioMayorQueCero(new BigDecimal("99999999.99"))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarPrecioMayorQueCero(new BigDecimal("100000000.00"))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarPrecioMayorQueCero(new BigDecimal("10.001"))
        );
    }

    @Test
    void validaLimitesDeQuilates() {
        assertDoesNotThrow(() -> ValidatorUtil.validarQuilates(new BigDecimal("99.99")));
        assertDoesNotThrow(() -> ValidatorUtil.validarQuilates(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarQuilates(new BigDecimal("100.00"))
        );
    }

    @Test
    void validaLongitudMaximaDeCamposOpcionales() {
        assertDoesNotThrow(() -> ValidatorUtil.validarLongitudMaxima(null, 150, "imagen"));
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidatorUtil.validarLongitudMaxima("x".repeat(151), 150, "imagen")
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