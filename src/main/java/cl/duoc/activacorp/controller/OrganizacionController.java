package cl.duoc.activacorp.controller;

import cl.duoc.activacorp.dto.OrganizacionResumen;
import cl.duoc.activacorp.model.Organizacion;
import cl.duoc.activacorp.service.OrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizaciones")
public class OrganizacionController {

    @Autowired
    private OrganizacionService organizacionService;

    // 1. GET: Listar con la cantidad de contactos
    @GetMapping
    public ResponseEntity<List<OrganizacionResumen>> listar() {
        return ResponseEntity.ok(organizacionService.listarResumen());
    }

    // 2. GET: Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Organizacion> buscarPorId(@PathVariable Long id) {
        Organizacion organizacion = organizacionService.buscarPorId(id);
        if (organizacion == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(organizacion);
    }

    // 3. POST: Registrar / Crear
    @PostMapping
    public ResponseEntity<Organizacion> registrar(@RequestBody Organizacion organizacion) {
        Organizacion nueva = organizacionService.registrar(organizacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}
