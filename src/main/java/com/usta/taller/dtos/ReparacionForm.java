package com.usta.taller.dtos;

import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.enums.TipoReparacion;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReparacionForm {

    private Long idReparacion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha = LocalDate.now();

    @NotNull(message = "El tipo de reparacion es obligatorio")
    private TipoReparacion tipo;

    @NotNull(message = "El vehiculo es obligatorio")
    private Long idVehiculo;

    private Long idMecanico;

    private String observaciones;

    public static ReparacionForm desde(ReparacionEntity reparacion) {
        ReparacionForm form = new ReparacionForm();
        form.setIdReparacion(reparacion.getIdReparacion());
        form.setFecha(reparacion.getFecha());
        form.setTipo(reparacion.getTipo());
        if (reparacion.getVehiculo() != null) {
            form.setIdVehiculo(reparacion.getVehiculo().getIdVehiculo());
        }
        if (reparacion.getMecanico() != null) {
            form.setIdMecanico(reparacion.getMecanico().getIdUsuario());
        }
        form.setObservaciones(reparacion.getObservaciones());
        return form;
    }
}
