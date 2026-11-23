package com.usta.taller.entities;

import com.usta.taller.entities.enums.ColorPlaca;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "vehiculos",
        uniqueConstraints = @UniqueConstraint(name = "uk_vehiculos_placa", columnNames = "placa")
)
@Getter
@Setter
@NoArgsConstructor
public class VehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{3}$", message = "La placa debe tener el formato ABC-123")
    @Column(name = "placa", nullable = false, length = 7)
    private String placa;

    @NotNull(message = "El color de la placa es obligatorio")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "color_placa", nullable = false, columnDefinition = "color_placa")
    private ColorPlaca colorPlaca;

    @NotBlank(message = "El nombre del dueno es obligatorio")
    @Size(max = 150, message = "El nombre del dueno no puede superar 150 caracteres")
    @Column(name = "nombre_dueno", nullable = false, length = 150)
    private String nombreDueno;

    @NotBlank(message = "El documento del dueno es obligatorio")
    @Size(max = 20, message = "El documento del dueno no puede superar 20 caracteres")
    @Column(name = "documento_dueno", nullable = false, length = 20)
    private String documentoDueno;

    @NotBlank(message = "El telefono del dueno es obligatorio")
    @Size(max = 20, message = "El telefono del dueno no puede superar 20 caracteres")
    @Column(name = "telefono_dueno", nullable = false, length = 20)
    private String telefonoDueno;

    @NotNull(message = "El modelo es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_id", nullable = false)
    private ModeloEntity modelo;

    @Column(name = "activo", nullable = false)
    private Boolean estadoVehiculo = Boolean.TRUE;
}
