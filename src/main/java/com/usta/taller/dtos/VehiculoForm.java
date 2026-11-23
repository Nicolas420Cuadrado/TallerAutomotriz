package com.usta.taller.dtos;

import com.usta.taller.entities.VehiculoEntity;
import com.usta.taller.entities.enums.ColorPlaca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehiculoForm {

    private Long idVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{3}$", message = "La placa debe tener el formato ABC-123")
    private String placa;

    @NotNull(message = "El color de la placa es obligatorio")
    private ColorPlaca colorPlaca;

    @NotBlank(message = "El nombre del dueno es obligatorio")
    @Size(max = 150, message = "El nombre del dueno no puede superar 150 caracteres")
    private String nombreDueno;

    @NotBlank(message = "El documento del dueno es obligatorio")
    @Size(max = 20, message = "El documento del dueno no puede superar 20 caracteres")
    private String documentoDueno;

    @NotBlank(message = "El telefono del dueno es obligatorio")
    @Size(max = 20, message = "El telefono del dueno no puede superar 20 caracteres")
    private String telefonoDueno;

    @NotNull(message = "El modelo es obligatorio")
    private Long idModelo;

    public static VehiculoForm desde(VehiculoEntity vehiculo) {
        VehiculoForm form = new VehiculoForm();
        form.setIdVehiculo(vehiculo.getIdVehiculo());
        form.setPlaca(vehiculo.getPlaca());
        form.setColorPlaca(vehiculo.getColorPlaca());
        form.setNombreDueno(vehiculo.getNombreDueno());
        form.setDocumentoDueno(vehiculo.getDocumentoDueno());
        form.setTelefonoDueno(vehiculo.getTelefonoDueno());
        if (vehiculo.getModelo() != null) {
            form.setIdModelo(vehiculo.getModelo().getIdModelo());
        }
        return form;
    }
}
