package com.usta.taller.entities.enums;

public enum TipoReparacion {
    MANTENIMIENTO_PREVENTIVO("Mantenimiento preventivo"),
    REPARACION_CORRECTIVA("Reparacion correctiva"),
    REVISION("Revision"),
    OTRO("Otro");

    private final String etiqueta;

    TipoReparacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
