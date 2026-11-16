package cl.duoc.activacorp.repository;

import cl.duoc.activacorp.dto.OrganizacionResumen;
import cl.duoc.activacorp.model.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {

    // Sirve para evitar organizaciones duplicadas
    boolean existsByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT new cl.duoc.activacorp.dto.OrganizacionResumen(o.id, o.nombre, o.tipo, o.comuna, COUNT(c.id))
            FROM Organizacion o
            LEFT JOIN Contacto c ON c.organizacion = o
            GROUP BY o.id, o.nombre, o.tipo, o.comuna
            ORDER BY o.nombre
            """)
    List<OrganizacionResumen> resumen();
}
