package cl.duoc.activacorp.controller;

import cl.duoc.activacorp.dto.LoginRequest;
import cl.duoc.activacorp.model.Usuario;
import cl.duoc.activacorp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. GET: Listar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // 2. GET: Listar solo ejecutivos
    @GetMapping("/ejecutivos")
    public ResponseEntity<List<Usuario>> listarEjecutivos() {
        return ResponseEntity.ok(usuarioService.listarEjecutivos());
    }

    // 3. GET: Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuario);
    }

    // 4. POST: Iniciar sesión (la clave no se devuelve en la respuesta)
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.iniciarSesion(request.correo(), request.clave());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(usuario);
    }
}
