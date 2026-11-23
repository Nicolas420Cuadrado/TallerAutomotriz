package com.usta.taller.models.daos;

import com.usta.taller.entities.ModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloDao extends JpaRepository<ModeloEntity, Long> {

    Optional<ModeloEntity> findByMarcaIgnoreCaseAndNombreModeloIgnoreCase(String marca, String nombreModelo);

    boolean existsByMarcaIgnoreCaseAndNombreModeloIgnoreCase(String marca, String nombreModelo);

    boolean existsByMarcaIgnoreCaseAndNombreModeloIgnoreCaseAndIdModeloNot(
            String marca, String nombreModelo, Long idModelo);

    List<ModeloEntity> findAllByOrderByMarcaAscNombreModeloAsc();

    List<ModeloEntity> findByEstadoModeloTrueOrderByMarcaAscNombreModeloAsc();

    @Query("""
            select m from ModeloEntity m
            where m.estadoModelo = true
              and (lower(m.marca) like lower(concat('%', :q, '%'))
                or lower(m.nombreModelo) like lower(concat('%', :q, '%')))
            order by m.marca, m.nombreModelo
            """)
    List<ModeloEntity> buscarActivos(@Param("q") String q);
}
