package com.usta.taller.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "repuestos",
        uniqueConstraints = @UniqueConstraint(name = "uk_repuestos_codigo_ean", columnNames = "codigo_ean")
)
@Getter
@Setter
@NoArgsConstructor
public class RepuestoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idRepuesto;

    @NotBlank(message = "El nombre del repuesto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener maximo 10 enteros y 2 decimales")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotBlank(message = "El codigo EAN es obligatorio")
    @Pattern(regexp = "^\\d{13}$", message = "El codigo EAN debe tener exactamente 13 digitos")
    @Column(name = "codigo_ean", nullable = false, length = 13)
    private String ean;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Size(max = 500, message = "La URL de la imagen no puede superar 500 caracteres")
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "activo", nullable = false)
    private Boolean estadoRepuesto = Boolean.TRUE;

    @ManyToMany
    @JoinTable(
            name = "modelos_repuestos",
            joinColumns = @JoinColumn(name = "repuesto_id"),
            inverseJoinColumns = @JoinColumn(name = "modelo_id")
    )
    private Set<ModeloEntity> modelos = new HashSet<>();
}
