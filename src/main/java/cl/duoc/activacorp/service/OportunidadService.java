package cl.duoc.activacorp.service;

import cl.duoc.activacorp.dto.AsignacionRequest;
import cl.duoc.activacorp.dto.CambioEstadoRequest;
import cl.duoc.activacorp.dto.OportunidadRequest;
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
import cl.duoc.activacorp.util.Permisos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OportunidadService {
    @Autowired
    private OportunidadRepository oportunidadRepository;

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    // 1. Listar: el supervisor ve todas; el ejecutivo solo las que tiene asignadas
    public List<Oportunidad> listarParaUsuario(Long usuarioId) {
        Usuario usuario = obtenerUsuario(usuarioId);
        if (usuario.getRol() == Rol.SUPERVISOR) {
            return oportunidadRepository.findAllByOrderByFechaEventoAsc();
        }
        return oportunidadRepository.findByResponsableIdOrderByFechaEventoAsc(usuario.getId());
    }

    // 2. Buscar por ID
    public Oportunidad buscarPorId(Long id) {
        return oportunidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oportunidad no encontrada"));
    }

    // 3. Historial de seguimientos (el más reciente primero)
    public List<Seguimiento> historial(Long id) {
        buscarPorId(id);
        return seguimientoRepository.findByOportunidadIdOrderByIdDesc(id);
    }

    // 4. Registrar (Crear): solo los ejecutivos registran oportunidades
    @Transactional
    public Oportunidad registrar(OportunidadRequest req) {
        Usuario usuario = obtenerUsuario(req.usuarioId());
        if (usuario.getRol() != Rol.EJECUTIVO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los ejecutivos pueden registrar oportunidades");
        }

        validar(req);
        exigir(!req.fechaEvento().isBefore(LocalDate.now()), "La fecha del evento no puede estar en el pasado");
        exigir(req.organizacionId() != null, "La organización es obligatoria");

        Organizacion organizacion = organizacionRepository.findById(req.organizacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La organización no existe"));

        Contacto contacto = null;
        if (req.contactoId() != null) {
            contacto = contactoRepository.findById(req.contactoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contacto no existe"));
            exigir(contacto.getOrganizacion().getId().equals(organizacion.getId()),
                    "El contacto no pertenece a la organización indicada");
        }

        Oportunidad oportunidad = Oportunidad.builder()
                .nombre(req.nombre().trim())
                .tipoEvento(req.tipoEvento().trim())
                .fechaEvento(req.fechaEvento())
                .ubicacion(req.ubicacion().trim())
                .comuna(req.comuna().trim())
                .publicoEsperado(req.publicoEsperado())
                .prioridad(req.prioridad() != null ? req.prioridad() : Prioridad.MEDIA)
                .estado(EstadoOportunidad.EVENTO_IDENTIFICADO)
                .observaciones(req.observaciones() != null ? req.observaciones().trim() : "")
                .organizacion(organizacion)
                .contacto(contacto)
                .responsable(usuario) // El ejecutivo que la crea queda como responsable inicial
                .creadoPor(usuario)
                .fechaCreacion(LocalDate.now())
                .fotoUri(req.fotoUri())
                .build();

        oportunidad = oportunidadRepository.save(oportunidad);
        registrarSeguimiento(oportunidad, usuario, "REGISTRO", "Oportunidad registrada",
                null, oportunidad.getEstado());
        return oportunidad;
    }

    // 5. Actualizar: edita los datos (incluida la prioridad) si el usuario puede gestionarla
    @Transactional
    public Oportunidad actualizar(Long id, OportunidadRequest req) {
        Oportunidad oportunidad = buscarPorId(id);
        Usuario usuario = obtenerUsuario(req.usuarioId());

        if (!Permisos.puedeGestionar(usuario, oportunidad.getResponsable())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta oportunidad");
        }

        validar(req);

        oportunidad.setNombre(req.nombre().trim());
        oportunidad.setTipoEvento(req.tipoEvento().trim());
        oportunidad.setFechaEvento(req.fechaEvento());
        oportunidad.setUbicacion(req.ubicacion().trim());
        oportunidad.setComuna(req.comuna().trim());
        oportunidad.setPublicoEsperado(req.publicoEsperado());
        if (req.prioridad() != null) {
            oportunidad.setPrioridad(req.prioridad());
        }
        if (req.observaciones() != null) {
            oportunidad.setObservaciones(req.observaciones().trim());
        }
        if (req.fotoUri() != null) {
            oportunidad.setFotoUri(req.fotoUri());
        }

        oportunidad = oportunidadRepository.save(oportunidad);
        registrarSeguimiento(oportunidad, usuario, "EDICION", "Datos de la oportunidad actualizados",
                null, null);
        return oportunidad;
    }

    // 6. Cambiar estado: valida permisos y transición antes de guardar
    @Transactional
    public Oportunidad cambiarEstado(Long id, CambioEstadoRequest req) {
        Oportunidad oportunidad = buscarPorId(id);
        Usuario usuario = obtenerUsuario(req.usuarioId());
        exigir(req.estado() != null, "El estado es obligatorio");

        if (!Permisos.puedeGestionar(usuario, oportunidad.getResponsable())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para gestionar esta oportunidad");
        }

        EstadoOportunidad estadoActual = oportunidad.getEstado();
        List<EstadoOportunidad> permitidos = Permisos.estadosSiguientes(estadoActual, usuario.getRol());

        if (!permitidos.contains(req.estado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transición no permitida: " + estadoActual.getEtiqueta() + " → " + req.estado().getEtiqueta());
        }

        oportunidad.setEstado(req.estado());
        oportunidad = oportunidadRepository.save(oportunidad);
        registrarSeguimiento(oportunidad, usuario, "CAMBIO_ESTADO",
                "Estado: " + estadoActual.getEtiqueta() + " → " + req.estado().getEtiqueta(),
                estadoActual, req.estado());
        return oportunidad;
    }

    // 7. Asignar: solo el supervisor, y no sobre oportunidades ya cerradas
    @Transactional
    public Oportunidad asignar(Long id, AsignacionRequest req) {
        Oportunidad oportunidad = buscarPorId(id);
        Usuario supervisor = obtenerUsuario(req.usuarioId());

        if (supervisor.getRol() != Rol.SUPERVISOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el supervisor puede asignar oportunidades");
        }
        if (Permisos.esEstadoFinal(oportunidad.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La oportunidad ya está cerrada");
        }

        Usuario ejecutivo = obtenerUsuario(req.ejecutivoId());
        exigir(ejecutivo.getRol() == Rol.EJECUTIVO, "El responsable debe ser un ejecutivo");

        oportunidad.setResponsable(ejecutivo);
        oportunidad = oportunidadRepository.save(oportunidad);
        registrarSeguimiento(oportunidad, supervisor, "ASIGNACION", "Asignada a " + ejecutivo.getNombre(),
                null, null);
        return oportunidad;
    }

    // ----- Métodos auxiliares -----

    private Usuario obtenerUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuarioId es obligatorio");
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Mismas validaciones que Validaciones.kt de la app
    private void validar(OportunidadRequest req) {
        exigir(req.nombre() != null && req.nombre().trim().length() >= 3,
                "El nombre debe tener al menos 3 caracteres");
        exigir(noVacio(req.tipoEvento()), "El tipo de evento es obligatorio");
        exigir(noVacio(req.ubicacion()), "La ubicación es obligatoria");
        exigir(noVacio(req.comuna()), "La comuna es obligatoria");
        exigir(req.publicoEsperado() != null && req.publicoEsperado() > 0,
                "El público esperado debe ser un número mayor que cero");
        exigir(req.fechaEvento() != null, "La fecha del evento es obligatoria");
    }

    private boolean noVacio(String texto) {
        return texto != null && !texto.isBlank();
    }

    private void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
        }
    }

    private void registrarSeguimiento(Oportunidad oportunidad, Usuario usuario, String accion,
                                      String descripcion, EstadoOportunidad anterior,
                                      EstadoOportunidad nuevo) {
        Seguimiento seguimiento = Seguimiento.builder()
                .oportunidad(oportunidad)
                .usuario(usuario)
                .fecha(LocalDateTime.now())
                .accion(accion)
                .descripcion(descripcion)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .build();
        seguimientoRepository.save(seguimiento);
    }
}
