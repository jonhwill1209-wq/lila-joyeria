package com.lilajoyeria.model;

import java.math.BigDecimal;

public class ModeloDominioTest {

    public static void main(String[] args) {
        probarCalculosDelPedido();
        probarValidaciones();

        System.out.println(
                "Todas las pruebas del modelo finalizaron correctamente."
        );
    }

    private static void probarCalculosDelPedido() {
        Categoria categoria = new Categoria(1, "Anillos");

        Joya joya = new Joya(
                1,
                "Anillo clásico",
                "Anillo utilizado para pruebas",
                "Oro",
                new BigDecimal("18.00"),
                new BigDecimal("25.50"),
                10,
                categoria
        );

        Usuario usuario = new Usuario(
                1,
                "Cliente de prueba",
                "cliente@correo.com",
                "clave123",
                RolUsuario.CLIENTE
        );

        Pedido pedido = new Pedido(
                usuario,
                BigDecimal.ZERO
        );

        DetallePedido detalle = new DetallePedido(
                pedido,
                joya,
                2,
                joya.getPrecio()
        );

        verificar(
                detalle.calcularSubtotal()
                        .compareTo(new BigDecimal("51.00")) == 0,
                "El subtotal debe ser 51.00"
        );

        pedido.agregarDetalle(detalle);

        verificar(
                pedido.getTotal()
                        .compareTo(new BigDecimal("51.00")) == 0,
                "El total del pedido debe ser 51.00"
        );

        verificar(
                pedido.getDetalles().size() == 1,
                "El pedido debe contener un detalle"
        );

        pedido.eliminarDetalle(detalle);

        verificar(
                pedido.getTotal()
                        .compareTo(BigDecimal.ZERO) == 0,
                "El total debe volver a cero al eliminar el detalle"
        );
    }

    private static void probarValidaciones() {
        boolean categoriaInvalida = false;

        try {
            new Categoria("   ");
        } catch (IllegalArgumentException exception) {
            categoriaInvalida = true;
        }

        verificar(
                categoriaInvalida,
                "Debe rechazarse una categoría sin nombre"
        );

        Joya joya = new Joya();
        boolean stockInvalido = false;

        try {
            joya.setStock(-1);
        } catch (IllegalArgumentException exception) {
            stockInvalido = true;
        }

        verificar(
                stockInvalido,
                "Debe rechazarse un stock negativo"
        );

        boolean emailInvalido = false;

        try {
            new Usuario(
                    "Usuario prueba",
                    "correo-invalido",
                    "clave123",
                    RolUsuario.CLIENTE
            );
        } catch (IllegalArgumentException exception) {
            emailInvalido = true;
        }

        verificar(
                emailInvalido,
                "Debe rechazarse un correo electrónico inválido"
        );
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }

        System.out.println("CORRECTO: " + mensaje);
    }
}