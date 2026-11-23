package com.usta.taller.models.services;

import com.usta.taller.dtos.VehiculoForm;
import com.usta.taller.entities.VehiculoEntity;

import java.util.List;
import java.util.Optional;

public interface VehiculoService {

    List<VehiculoEntity> listarActivos();

    List<VehiculoEntity> listarTodos();

    List<VehiculoEntity> buscar(String q);

    Optional<VehiculoEntity> buscarPorId(Long id);

    VehiculoEntity obtenerPorId(Long id);

    Optional<VehiculoEntity> buscarPorPlaca(String placa);

    VehiculoEntity guardar(VehiculoForm form);

    void cambiarEstado(Long id);

    long contarActivos();
}
