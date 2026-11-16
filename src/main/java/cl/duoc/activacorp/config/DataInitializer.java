package cl.duoc.activacorp.config;

import cl.duoc.activacorp.model.Contacto;
import cl.duoc.activacorp.model.EstadoOportunidad;
import cl.duoc.activacorp.model.Oportunidad;
import cl.duoc.activacorp.model.Organizacion;
import cl.duoc.activacorp.model.Prioridad;
import cl.duoc.activacorp.model.Rol;
import cl.duoc.activacorp.model.Seguimiento;
import cl.duoc.activacorp.model.Usuario;
import cl.duoc.activacorp.repository.ContactoRepository;
import cl.duoc.activacorp.repository.OportunidadRepository;
import cl.duoc.activacorp.repository.OrganizacionRepository;
import cl.duoc.activacorp.repository.SeguimientoRepository;
import cl.duoc.activacorp.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Carga los mismos datos ficticios que DatosIniciales.kt, solo si la base está vacía
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatosDemo(UsuarioRepository usuarioRepository,
                                      OrganizacionRepository organizacionRepository,
                                      ContactoRepository contactoRepository,
                                      OportunidadRepository oportunidadRepository,
                                      SeguimientoRepository seguimientoRepository) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            // ---------- Usuarios (3 ejecutivos y 1 supervisor) ----------
            Usuario camila = usuarioRepository.save(new Usuario(null, "Camila Rojas", "camila.rojas@agencia.cl", "ejecutivo123", Rol.EJECUTIVO));
            Usuario diego = usuarioRepository.save(new Usuario(null, "Diego Soto", "diego.soto@agencia.cl", "ejecutivo123", Rol.EJECUTIVO));
            Usuario valentina = usuarioRepository.save(new Usuario(null, "Valentina Pérez", "valentina.perez@agencia.cl", "ejecutivo123", Rol.EJECUTIVO));
            Usuario marcos = usuarioRepository.save(new Usuario(null, "Marcos Fuentes", "marcos.fuentes@agencia.cl", "supervisor123", Rol.SUPERVISOR));

            // ---------- Organizaciones ----------
            Organizacion municipalidad = organizacionRepository.save(new Organizacion(null, "Municipalidad Valle Sur", "Municipalidad", "Puente Alto"));
            Organizacion constructora = organizacionRepository.save(new Organizacion(null, "Constructora Andes Norte", "Empresa privada", "Providencia"));
            Organizacion colegio = organizacionRepository.save(new Organizacion(null, "Colegio San Rafael", "Institución educativa", "Maipú"));
            Organizacion retail = organizacionRepository.save(new Organizacion(null, "Retail Mercado Centro", "Empresa privada", "Santiago"));

            // ---------- Contactos ----------
            Contacto cMunicipalidad = contactoRepository.save(new Contacto(null, municipalidad, "Andrea Molina", "Encargada de eventos", "andrea.molina@ejemplo.cl", "+56 9 5550 0001"));
            Contacto cConstructora = contactoRepository.save(new Contacto(null, constructora, "Rodrigo Vera", "Jefe de bienestar", "rodrigo.vera@ejemplo.cl", "+56 9 5550 0002"));
            Contacto cColegio = contactoRepository.save(new Contacto(null, colegio, "Paula Herrera", "Coordinadora de actividades", "paula.herrera@ejemplo.cl", "+56 9 5550 0003"));
            Contacto cRetail = contactoRepository.save(new Contacto(null, retail, "Nicolás Araya", "Gerente de marketing", "nicolas.araya@ejemplo.cl", "+56 9 5550 0004"));

            // ---------- Oportunidades de prueba (fechas relativas a hoy) ----------
            LocalDate hoy = LocalDate.now();
            List<Oportunidad> oportunidades = oportunidadRepository.saveAll(List.of(
                    oportunidad("Feria de beneficios municipal", "Feria municipal", hoy.plusDays(7),
                            "Plaza de Armas", "Puente Alto", 800, Prioridad.ALTA,
                            EstadoOportunidad.CONTACTO_PENDIENTE, municipalidad, cMunicipalidad, camila, camila),
                    oportunidad("Jornada de bienestar corporativo", "Jornada de bienestar", hoy.plusDays(12),
                            "Casa matriz", "Providencia", 250, Prioridad.MEDIA,
                            EstadoOportunidad.PROPUESTA_ENVIADA, constructora, cConstructora, camila, camila),
                    oportunidad("Feria empresarial regional", "Feria empresarial", hoy.plusDays(25),
                            "Centro de eventos", "Santiago", 1500, Prioridad.ALTA,
                            EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, retail, cRetail, diego, diego),
                    oportunidad("Semana de beneficios del colegio", "Actividad institucional", hoy.plusDays(20),
                            "Patio central", "Maipú", 400, Prioridad.BAJA,
                            EstadoOportunidad.EVENTO_IDENTIFICADO, colegio, cColegio, valentina, valentina),
                    // Sin responsable: sirve para probar la asignación del supervisor
                    oportunidad("Aniversario comunal", "Actividad municipal", hoy.plusDays(35),
                            "Estadio municipal", "Puente Alto", 3000, Prioridad.ALTA,
                            EstadoOportunidad.EVENTO_IDENTIFICADO, municipalidad, null, null, marcos)
            ));

            for (Oportunidad o : oportunidades) {
                seguimientoRepository.save(Seguimiento.builder()
                        .oportunidad(o)
                        .usuario(o.getCreadoPor())
                        .fecha(LocalDateTime.now())
                        .accion("REGISTRO")
                        .descripcion("Oportunidad registrada (dato de prueba)")
                        .estadoNuevo(o.getEstado())
                        .build());
            }
        };
    }

    private static Oportunidad oportunidad(String nombre, String tipoEvento, LocalDate fechaEvento,
                                           String ubicacion, String comuna, int publicoEsperado,
                                           Prioridad prioridad, EstadoOportunidad estado,
                                           Organizacion organizacion, Contacto contacto,
                                           Usuario responsable, Usuario creadoPor) {
        return Oportunidad.builder()
                .nombre(nombre)
                .tipoEvento(tipoEvento)
                .fechaEvento(fechaEvento)
                .ubicacion(ubicacion)
                .comuna(comuna)
                .publicoEsperado(publicoEsperado)
                .prioridad(prioridad)
                .estado(estado)
                .observaciones("")
                .organizacion(organizacion)
                .contacto(contacto)
                .responsable(responsable)
                .creadoPor(creadoPor)
                .fechaCreacion(LocalDate.now())
                .build();
    }
}
