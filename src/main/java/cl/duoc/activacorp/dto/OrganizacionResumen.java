package cl.duoc.activacorp.dto;

// Organización junto con la cantidad de contactos que tiene
public record OrganizacionResumen(Long id, String nombre, String tipo, String comuna, Long totalContactos) {
}
