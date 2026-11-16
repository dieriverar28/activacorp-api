package cl.duoc.activacorp.service;

import cl.duoc.activacorp.dto.OrganizacionResumen;
import cl.duoc.activacorp.model.Organizacion;
import cl.duoc.activacorp.repository.OrganizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrganizacionService {
    @Autowired
    private OrganizacionRepository organizacionRepository;

    // 1. Listar con la cantidad de contactos de cada una
    public List<OrganizacionResumen> listarResumen() {
        return organizacionRepository.resumen();
    }

    // 2. Buscar por ID
    public Organizacion buscarPorId(Long id) {
        return organizacionRepository.findById(id).orElse(null);
    }

    // 3. Registrar (Crear)
    public Organizacion registrar(Organizacion organizacion) {
        if (organizacion.getNombre() == null || organizacion.getNombre().trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre debe tener al menos 3 caracteres");
        }
        if (vacio(organizacion.getTipo()) || vacio(organizacion.getComuna())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo y la comuna son obligatorios");
        }
        if (organizacionRepository.existsByNombreIgnoreCase(organizacion.getNombre().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una organización con ese nombre");
        }

        organizacion.setId(null);
        organizacion.setNombre(organizacion.getNombre().trim());
        organizacion.setTipo(organizacion.getTipo().trim());
        organizacion.setComuna(organizacion.getComuna().trim());
        return organizacionRepository.save(organizacion);
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
