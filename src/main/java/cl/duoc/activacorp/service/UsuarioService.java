package cl.duoc.activacorp.service;

import cl.duoc.activacorp.model.Rol;
import cl.duoc.activacorp.model.Usuario;
import cl.duoc.activacorp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Listar todos
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // 2. Buscar por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // 3. Listar solo los ejecutivos (para que el supervisor asigne oportunidades)
    public List<Usuario> listarEjecutivos() {
        return usuarioRepository.findByRolOrderByNombre(Rol.EJECUTIVO);
    }

    // 4. Iniciar sesión: devuelve null si las credenciales no coinciden
    public Usuario iniciarSesion(String correo, String clave) {
        if (correo == null || clave == null) {
            return null;
        }
        return usuarioRepository
                .findByCorreoIgnoreCaseAndClave(correo.trim(), clave)
                .orElse(null);
    }
}
