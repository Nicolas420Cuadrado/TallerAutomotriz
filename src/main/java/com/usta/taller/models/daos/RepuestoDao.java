package com.usta.taller.models.daos;

import com.usta.taller.entities.RepuestoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepuestoDao extends JpaRepository<RepuestoEntity, Long> {

    boolean existsByEan(String ean);

    boolean existsByEanAndIdRepuestoNot(String ean, Long idRepuesto);

    @EntityGraph(attributePaths = "modelos")
    Optional<RepuestoEntity> findById(Long id);

    @EntityGraph(attributePaths = "modelos")
    List<RepuestoEntity> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "modelos")
    List<RepuestoEntity> findByEstadoRepuestoTrueOrderByNombreAsc();

    long countByEstadoRepuestoTrue();

    @EntityGraph(attributePaths = "modelos")
    @Query("""
            select r from RepuestoEntity r
            where r.estadoRepuesto = true
              and (lower(r.nombre) like lower(concat('%', :q, '%'))
                or lower(r.ean) like lower(concat('%', :q, '%'))
                or lower(coalesce(r.descripcion, '')) like lower(concat('%', :q, '%')))
            order by r.nombre
            """)
    List<RepuestoEntity> buscarActivos(@Param("q") String q);

    @EntityGraph(attributePaths = "modelos")
    @Query("""
            select distinct r from RepuestoEntity r
            join r.modelos m
            where m.idModelo = :idModelo
              and r.estadoRepuesto = true
            order by r.nombre
            """)
    List<RepuestoEntity> buscarActivosPorModelo(@Param("idModelo") Long idModelo);
}
