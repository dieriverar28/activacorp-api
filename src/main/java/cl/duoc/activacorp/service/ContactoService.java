package cl.duoc.activacorp.service;

import cl.duoc.activacorp.dto.ContactoRequest;
import cl.duoc.activacorp.model.Contacto;
import cl.duoc.activacorp.model.Organizacion;
import cl.duoc.activacorp.repository.ContactoRepository;
import cl.duoc.activacorp.repository.OrganizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ContactoService {
    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    // Acepta +56 9 5550 0001 o 955500001 (entre 8 y 12 dígitos, con + opcional al inicio)
    private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9]{8,12}$");

    // 1. Listar los contactos de una organización
    public List<Contacto> listarPorOrganizacion(Long organizacionId) {
        buscarOrganizacion(organizacionId);
        return contactoRepository.findByOrganizacionIdOrderByNombre(organizacionId);
    }

    // 2. Registrar (Crear) un contacto dentro de una organización
    public Contacto registrar(Long organizacionId, ContactoRequest req) {
        Organizacion organizacion = buscarOrganizacion(organizacionId);

        if (req.nombre() == null || req.nombre().trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre debe tener al menos 3 caracteres");
        }
        if (req.cargo() == null || req.cargo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cargo es obligatorio");
        }
        if (req.correo() == null || !CORREO.matcher(req.correo().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
        }
        if (req.telefono() == null || !TELEFONO.matcher(limpiarTelefono(req.telefono())).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El teléfono no es válido");
        }
        if (contactoRepository.existsByOrganizacionIdAndCorreoIgnoreCase(organizacionId, req.correo().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un contacto con ese correo en la organización");
        }

        Contacto contacto = new Contacto(
                null,
                organizacion,
                req.nombre().trim(),
                req.cargo().trim(),
                req.correo().trim(),
                req.telefono().trim()
        );
        return contactoRepository.save(contacto);
    }

    private Organizacion buscarOrganizacion(Long organizacionId) {
        return organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organización no encontrada"));
    }

    private String limpiarTelefono(String telefono) {
        return telefono.replace(" ", "").replace("-", "");
    }
}
