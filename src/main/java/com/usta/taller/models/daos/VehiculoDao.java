package com.usta.taller.models.daos;

import com.usta.taller.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoDao extends JpaRepository<VehiculoEntity, Long> {

    Optional<VehiculoEntity> findByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCaseAndIdVehiculoNot(String placa, Long idVehiculo);

    List<VehiculoEntity> findAllByOrderByPlacaAsc();

    List<VehiculoEntity> findByEstadoVehiculoTrueOrderByPlacaAsc();

    long countByEstadoVehiculoTrue();

    @Query("""
            select v from VehiculoEntity v
            where v.estadoVehiculo = true
              and (lower(v.placa) like lower(concat('%', :q, '%'))
                or lower(v.nombreDueno) like lower(concat('%', :q, '%'))
                or lower(v.documentoDueno) like lower(concat('%', :q, '%'))
                or lower(v.modelo.marca) like lower(concat('%', :q, '%'))
                or lower(v.modelo.nombreModelo) like lower(concat('%', :q, '%')))
            order by v.placa
            """)
    List<VehiculoEntity> buscarActivos(@Param("q") String q);
}
