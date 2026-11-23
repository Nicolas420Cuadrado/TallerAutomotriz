package com.usta.taller.entities.enums;

public enum EstadoReparacion {
    EN_PROCESO("En proceso"),
    TERMINADO("Terminado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoReparacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
