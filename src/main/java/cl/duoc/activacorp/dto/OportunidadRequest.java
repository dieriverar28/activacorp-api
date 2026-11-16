package cl.duoc.activacorp.dto;

import cl.duoc.activacorp.model.Prioridad;

import java.time.LocalDate;

// Sirve para crear (POST) y para editar (PUT) una oportunidad.
// usuarioId es quien realiza la acción (reemplaza a la clase Sesion de la app).
public record OportunidadRequest(
        Long usuarioId,
        String nombre,
        String tipoEvento,
        LocalDate fechaEvento,
        String ubicacion,
        String comuna,
        Integer publicoEsperado,
        Prioridad prioridad,
        String observaciones,
        Long organizacionId,
        Long contactoId,
        String fotoUri
) {
}
