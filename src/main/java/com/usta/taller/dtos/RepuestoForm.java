package com.usta.taller.dtos;

import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.entities.RepuestoEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RepuestoForm {

    private Long idRepuesto;

    @NotBlank(message = "El nombre del repuesto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener maximo 10 enteros y 2 decimales")
    private BigDecimal precio;

    @NotBlank(message = "El codigo EAN es obligatorio")
    @Pattern(regexp = "^\\d{13}$", message = "El codigo EAN debe tener exactamente 13 digitos")
    private String ean;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Size(max = 500, message = "La URL de la imagen no puede superar 500 caracteres")
    private String imagenUrl;

    @NotEmpty(message = "Debe seleccionar al menos un modelo compatible")
    private Set<Long> idsModelos = new HashSet<>();

    public static RepuestoForm desde(RepuestoEntity repuesto) {
        RepuestoForm form = new RepuestoForm();
        form.setIdRepuesto(repuesto.getIdRepuesto());
        form.setNombre(repuesto.getNombre());
        form.setDescripcion(repuesto.getDescripcion());
        form.setPrecio(repuesto.getPrecio());
        form.setEan(repuesto.getEan());
        form.setStock(repuesto.getStock());
        form.setImagenUrl(repuesto.getImagenUrl());
        if (repuesto.getModelos() != null) {
            for (ModeloEntity modelo : repuesto.getModelos()) {
                form.getIdsModelos().add(modelo.getIdModelo());
            }
        }
        return form;
    }
}
