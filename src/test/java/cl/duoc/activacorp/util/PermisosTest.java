package cl.duoc.activacorp.util;

import cl.duoc.activacorp.model.EstadoOportunidad;
import cl.duoc.activacorp.model.Rol;
import cl.duoc.activacorp.model.Usuario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Prueba las reglas de negocio sin necesitar la base de datos
class PermisosTest {

    @Test
    void ejecutivoAvanzaPasoAPaso() {
        assertEquals(
                List.of(EstadoOportunidad.CONTACTO_REALIZADO),
                Permisos.estadosSiguientes(EstadoOportunidad.CONTACTO_PENDIENTE, Rol.EJECUTIVO)
        );
    }

    @Test
    void ejecutivoNoPuedeConfirmarNiDescartar() {
        List<EstadoOportunidad> permitidos =
                Permisos.estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.EJECUTIVO);
        assertTrue(permitidos.isEmpty());
    }

    @Test
    void supervisorConfirmaSoloDesdeEnEspera() {
        assertTrue(Permisos.estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.SUPERVISOR)
                .contains(EstadoOportunidad.PARTICIPACION_CONFIRMADA));
        assertFalse(Permisos.estadosSiguientes(EstadoOportunidad.PROPUESTA_ENVIADA, Rol.SUPERVISOR)
                .contains(EstadoOportunidad.PARTICIPACION_CONFIRMADA));
    }

    @Test
    void estadoFinalNoTieneTransiciones() {
        assertTrue(Permisos.estadosSiguientes(EstadoOportunidad.OPORTUNIDAD_DESCARTADA, Rol.SUPERVISOR).isEmpty());
        assertTrue(Permisos.estadosSiguientes(EstadoOportunidad.PARTICIPACION_CONFIRMADA, Rol.SUPERVISOR).isEmpty());
    }

    @Test
    void ejecutivoSoloGestionaLoSuyo() {
        Usuario camila = new Usuario(1L, "Camila", "camila@agencia.cl", "x", Rol.EJECUTIVO);
        Usuario diego = new Usuario(2L, "Diego", "diego@agencia.cl", "x", Rol.EJECUTIVO);
        Usuario marcos = new Usuario(3L, "Marcos", "marcos@agencia.cl", "x", Rol.SUPERVISOR);

        assertTrue(Permisos.puedeGestionar(camila, camila));
        assertFalse(Permisos.puedeGestionar(camila, diego));
        assertFalse(Permisos.puedeGestionar(camila, null));
        assertTrue(Permisos.puedeGestionar(marcos, diego));
    }
}
