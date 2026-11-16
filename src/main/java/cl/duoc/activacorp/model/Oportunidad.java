package cl.duoc.activacorp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oportunidades")
public class Oportunidad {
    @Column(name = "id_oportunidad")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String tipoEvento;

    @Column(nullable = false)
    private LocalDate fechaEvento;

    @Column(nullable = false, length = 100)
    private String ubicacion;

    @Column(nullable = false, length = 40)
    private String comuna;

    @Column(nullable = false)
    private int publicoEsperado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOportunidad estado;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private Organizacion organizacion;

    // Contacto opcional
    @ManyToOne
    @JoinColumn(name = "id_contacto")
    private Contacto contacto;

    // Ejecutivo responsable (null mientras no esté asignada)
    @ManyToOne
    @JoinColumn(name = "id_responsable")
    private Usuario responsable;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(nullable = false)
    private LocalDate fechaCreacion;

    // En el proyecto del instituto la foto es simulada: solo se guarda su ruta como texto
    @Column(length = 255)
    private String fotoUri;
}
