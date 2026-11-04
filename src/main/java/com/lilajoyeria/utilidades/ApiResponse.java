package com.lilajoyeria.utilidades;

public class ApiResponse<T> {
    private boolean exito;
    private String mensaje;
    private T datos;

    public ApiResponse(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }
}
