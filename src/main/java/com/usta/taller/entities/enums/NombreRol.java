package com.usta.taller.entities.enums;

public enum NombreRol {
    ADMINISTRADOR("Administrador"),
    MECANICO("Mecanico");

    private final String etiqueta;

    NombreRol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
