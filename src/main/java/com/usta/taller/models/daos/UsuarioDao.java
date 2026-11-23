package com.usta.taller.models.daos;

import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.entities.enums.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioDao extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdUsuarioNot(String email, Long idUsuario);

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdUsuarioNot(String documento, Long idUsuario);

    List<UsuarioEntity> findAllByOrderByNombreAsc();

    List<UsuarioEntity> findByEstadoUsuarioTrueOrderByNombreAsc();

    List<UsuarioEntity> findByEstadoUsuarioTrueAndRol_NombreRolOrderByNombreAsc(NombreRol nombreRol);

    long countByEstadoUsuarioTrue();
}
