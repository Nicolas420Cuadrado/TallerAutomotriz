package com.usta.taller.entities;

import com.usta.taller.entities.enums.EstadoReparacion;
import com.usta.taller.entities.enums.TipoReparacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "reparaciones")
@Getter
@Setter
@NoArgsConstructor
public class ReparacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idReparacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "El tipo de reparacion es obligatorio")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_reparacion")
    private TipoReparacion tipo;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false, columnDefinition = "estado_reparacion")
    private EstadoReparacion estado = EstadoReparacion.EN_PROCESO;

    @NotNull(message = "El vehiculo es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private VehiculoEntity vehiculo;

    @NotNull(message = "El mecanico es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private UsuarioEntity mecanico;

    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;
}
