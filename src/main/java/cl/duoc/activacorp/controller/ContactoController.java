package cl.duoc.activacorp.controller;

import cl.duoc.activacorp.dto.ContactoRequest;
import cl.duoc.activacorp.model.Contacto;
import cl.duoc.activacorp.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizaciones/{organizacionId}/contactos")
public class ContactoController {

    @Autowired
    private ContactoService contactoService;

    // 1. GET: Contactos de una organización
    @GetMapping
    public ResponseEntity<List<Contacto>> listar(@PathVariable Long organizacionId) {
        return ResponseEntity.ok(contactoService.listarPorOrganizacion(organizacionId));
    }

    // 2. POST: Crear un contacto dentro de la organización
    @PostMapping
    public ResponseEntity<Contacto> registrar(@PathVariable Long organizacionId,
                                              @RequestBody ContactoRequest request) {
        Contacto nuevo = contactoService.registrar(organizacionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}
