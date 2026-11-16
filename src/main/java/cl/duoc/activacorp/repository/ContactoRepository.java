package cl.duoc.activacorp.repository;

import cl.duoc.activacorp.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findByOrganizacionIdOrderByNombre(Long organizacionId);

    // Sirve para evitar contactos repetidos dentro de una misma organización
    boolean existsByOrganizacionIdAndCorreoIgnoreCase(Long organizacionId, String correo);
}
