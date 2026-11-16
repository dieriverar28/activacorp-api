package cl.duoc.activacorp.dto;

import cl.duoc.activacorp.model.EstadoOportunidad;

public record CambioEstadoRequest(Long usuarioId, EstadoOportunidad estado) {
}
