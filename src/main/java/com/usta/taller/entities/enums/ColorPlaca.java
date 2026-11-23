package com.usta.taller.entities.enums;

public enum ColorPlaca {
    AMARILLA("Amarilla"),
    BLANCA("Blanca");

    private final String etiqueta;

    ColorPlaca(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
