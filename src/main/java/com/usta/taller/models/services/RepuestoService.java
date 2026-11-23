package com.usta.taller.models.services;

import com.usta.taller.dtos.RepuestoForm;
import com.usta.taller.entities.RepuestoEntity;

import java.util.List;
import java.util.Optional;

public interface RepuestoService {

    List<RepuestoEntity> listarActivos();

    List<RepuestoEntity> listarTodos();

    List<RepuestoEntity> buscar(String q);

    Optional<RepuestoEntity> buscarPorId(Long id);

    RepuestoEntity obtenerPorId(Long id);

    RepuestoEntity guardar(RepuestoForm form);

    void cambiarEstado(Long id);

    List<RepuestoEntity> buscarPorModelo(Long idModelo);

    long contarActivos();
}
