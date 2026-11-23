package com.usta.taller.models.daos;

import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.enums.EstadoReparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReparacionDao extends JpaRepository<ReparacionEntity, Long> {

    List<ReparacionEntity> findAllByOrderByFechaDescIdReparacionDesc();

    List<ReparacionEntity> findByMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(Long idUsuario);

    List<ReparacionEntity> findByVehiculo_IdVehiculoOrderByFechaDescIdReparacionDesc(Long idVehiculo);

    List<ReparacionEntity> findByVehiculo_IdVehiculoAndMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(
            Long idVehiculo, Long idUsuario);

    Optional<ReparacionEntity> findByIdReparacionAndMecanico_IdUsuario(Long idReparacion, Long idUsuario);

    long countByEstado(EstadoReparacion estado);
}
