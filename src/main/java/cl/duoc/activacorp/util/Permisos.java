package cl.duoc.activacorp.util;

import cl.duoc.activacorp.model.EstadoOportunidad;
import cl.duoc.activacorp.model.Rol;
import cl.duoc.activacorp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

// Mismas reglas de negocio que Permisos.kt de la app Android
public final class Permisos {

    private Permisos() {
    }

    public static boolean esEstadoFinal(EstadoOportunidad estado) {
        return estado == EstadoOportunidad.PARTICIPACION_CONFIRMADA
                || estado == EstadoOportunidad.OPORTUNIDAD_DESCARTADA;
    }

    // El supervisor accede a todo; el ejecutivo solo a las oportunidades que tiene asignadas
    public static boolean puedeGestionar(Usuario usuario, Usuario responsable) {
        return usuario.getRol() == Rol.SUPERVISOR
                || (responsable != null && responsable.getId().equals(usuario.getId()));
    }

    // Estados a los que puede pasar una oportunidad según el rol (transiciones permitidas)
    public static List<EstadoOportunidad> estadosSiguientes(EstadoOportunidad actual, Rol rol) {
        if (esEstadoFinal(actual)) {
            return List.of();
        }

        if (rol == Rol.EJECUTIVO) {
            // El ejecutivo avanza paso a paso, pero NO puede confirmar ni descartar
            return switch (actual) {
                case EVENTO_IDENTIFICADO -> List.of(EstadoOportunidad.CONTACTO_PENDIENTE);
                case CONTACTO_PENDIENTE -> List.of(EstadoOportunidad.CONTACTO_REALIZADO);
                case CONTACTO_REALIZADO -> List.of(EstadoOportunidad.PROPUESTA_ENVIADA);
                case PROPUESTA_ENVIADA -> List.of(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA);
                default -> List.of();
            };
        }

        // El supervisor decide: confirma (solo si está en espera de respuesta) o descarta
        List<EstadoOportunidad> decisiones = new ArrayList<>();
        if (actual == EstadoOportunidad.EN_ESPERA_DE_RESPUESTA) {
            decisiones.add(EstadoOportunidad.PARTICIPACION_CONFIRMADA);
        }
        decisiones.add(EstadoOportunidad.OPORTUNIDAD_DESCARTADA);
        return decisiones;
    }
}
