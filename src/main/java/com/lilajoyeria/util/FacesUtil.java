package com.lilajoyeria.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;

/**
 * Utilidades comunes para los managed beans: mensajes globales y reinicio
 * de formularios. Evita repetir el mismo código en cada bean.
 */
public final class FacesUtil {

    private FacesUtil() {
    }

    public static void info(String texto) {
        agregar(FacesMessage.SEVERITY_INFO, texto);
    }

    public static void aviso(String texto) {
        agregar(FacesMessage.SEVERITY_WARN, texto);
    }

    public static void error(String texto) {
        agregar(FacesMessage.SEVERITY_ERROR, texto);
    }

    private static void agregar(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severidad, texto, texto));
    }

    /** Conserva los mensajes a través de un redirect (faces-redirect=true). */
    public static void mantenerMensajes() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);
    }

    /**
     * Borra los valores enviados/locales de todos los campos de un formulario.
     * Sin esto, un formulario que falló la validación sigue mostrando lo que
     * el usuario escribió aunque el bean ya tenga otros datos (p. ej. al
     * pulsar "Editar" en otra fila de la tabla).
     */
    public static void reiniciarEntradas(String idFormulario) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        UIComponent formulario = contexto.getViewRoot().findComponent(idFormulario);

        if (formulario == null) {
            return;
        }

        formulario.visitTree(
                VisitContext.createVisitContext(contexto),
                (VisitContext visita, UIComponent componente) -> {
                    if (componente instanceof EditableValueHolder entrada) {
                        entrada.resetValue();
                    }
                    return VisitResult.ACCEPT;
                }
        );
    }
}
