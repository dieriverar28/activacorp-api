package cl.duoc.activacorp.dto;

// usuarioId es el supervisor que asigna; ejecutivoId es el nuevo responsable
public record AsignacionRequest(Long usuarioId, Long ejecutivoId) {
}
