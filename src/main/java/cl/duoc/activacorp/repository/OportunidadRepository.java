package cl.duoc.activacorp.repository;

import cl.duoc.activacorp.model.Oportunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OportunidadRepository extends JpaRepository<Oportunidad, Long> {

    List<Oportunidad> findAllByOrderByFechaEventoAsc();

    List<Oportunidad> findByResponsableIdOrderByFechaEventoAsc(Long responsableId);
}
