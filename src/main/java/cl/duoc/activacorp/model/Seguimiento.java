package cl.duoc.activacorp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seguimientos")
public class Seguimiento {
    @Column(name = "id_seguimiento")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No se devuelve en el JSON: el historial se consulta desde /oportunidades/{id}/seguimientos
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_oportunidad", nullable = false)
    private Oportunidad oportunidad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // REGISTRO, EDICION, CAMBIO_ESTADO o ASIGNACION
    @Column(nullable = false, length = 20)
    private String accion;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EstadoOportunidad estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EstadoOportunidad estadoNuevo;

    private LocalDate proximaFecha;
}
