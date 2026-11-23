package com.usta.taller.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "modelos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_modelos_marca_nombre",
                columnNames = {"marca", "nombre"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "idModelo")
public class ModeloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idModelo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede superar 100 caracteres")
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank(message = "El nombre del modelo es obligatorio")
    @Size(max = 100, message = "El nombre del modelo no puede superar 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombreModelo;

    @Column(name = "activo", nullable = false)
    private Boolean estadoModelo = Boolean.TRUE;

    public String getDescripcion() {
        return marca + " " + nombreModelo;
    }
}
