package cl.duoc.activacorp.repository;

import cl.duoc.activacorp.model.Rol;
import cl.duoc.activacorp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCaseAndClave(String correo, String clave);

    List<Usuario> findByRolOrderByNombre(Rol rol);
}
