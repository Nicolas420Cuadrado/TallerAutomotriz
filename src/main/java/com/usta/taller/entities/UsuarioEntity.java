package com.usta.taller.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuarios_documento", columnNames = "documento"),
                @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    @Column(name = "documento", nullable = false, length = 20)
    private String documento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    @Column(name = "correo", nullable = false, length = 150)
    private String email;

    @Size(max = 255, message = "La clave no puede superar 255 caracteres")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String clave;

    @Column(name = "activo", nullable = false)
    private Boolean estadoUsuario = Boolean.TRUE;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public String getNombreCompleto() {
        return nombre;
    }
}
