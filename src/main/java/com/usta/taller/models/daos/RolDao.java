package com.usta.taller.models.daos;

import com.usta.taller.entities.RolEntity;
import com.usta.taller.entities.enums.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolDao extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombreRol(NombreRol nombreRol);
}
