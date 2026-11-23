package com.usta.taller.entities;

import com.usta.taller.entities.enums.NombreRol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_roles_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idRol;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "nombre", nullable = false, columnDefinition = "rol_nombre")
    private NombreRol nombreRol;

    public RolEntity(NombreRol nombreRol) {
        this.nombreRol = nombreRol;
    }
}
