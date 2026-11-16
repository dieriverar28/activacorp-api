package cl.duoc.activacorp.controller;

import cl.duoc.activacorp.dto.AsignacionRequest;
import cl.duoc.activacorp.dto.CambioEstadoRequest;
import cl.duoc.activacorp.dto.OportunidadRequest;
import cl.duoc.activacorp.model.Oportunidad;
import cl.duoc.activacorp.model.Seguimiento;
import cl.duoc.activacorp.service.OportunidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oportunidades")
public class OportunidadController {

    @Autowired
    private OportunidadService oportunidadService;

    // 1. GET: Listar según el usuario (supervisor: todas; ejecutivo: las suyas)
    @GetMapping
    public ResponseEntity<List<Oportunidad>> listar(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(oportunidadService.listarParaUsuario(usuarioId));
    }

    // 2. GET: Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Oportunidad> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadService.buscarPorId(id));
    }

    // 3. GET: Historial de seguimientos
    @GetMapping("/{id}/seguimientos")
    public ResponseEntity<List<Seguimiento>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadService.historial(id));
    }

    // 4. POST: Registrar / Crear
    @PostMapping
    public ResponseEntity<Oportunidad> registrar(@RequestBody OportunidadRequest request) {
        Oportunidad nueva = oportunidadService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // 5. PUT: Actualizar datos y prioridad
    @PutMapping("/{id}")
    public ResponseEntity<Oportunidad> actualizar(@PathVariable Long id,
                                                  @RequestBody OportunidadRequest request) {
        return ResponseEntity.ok(oportunidadService.actualizar(id, request));
    }

    // 6. PUT: Cambiar estado
    @PutMapping("/{id}/estado")
    public ResponseEntity<Oportunidad> cambiarEstado(@PathVariable Long id,
                                                     @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(oportunidadService.cambiarEstado(id, request));
    }

    // 7. PUT: Asignar o reasignar ejecutivo (solo supervisor)
    @PutMapping("/{id}/asignacion")
    public ResponseEntity<Oportunidad> asignar(@PathVariable Long id,
                                               @RequestBody AsignacionRequest request) {
        return ResponseEntity.ok(oportunidadService.asignar(id, request));
    }
}
