package com.usta.taller.models.services;

import com.usta.taller.entities.ModeloEntity;

import java.util.List;
import java.util.Optional;

public interface ModeloService {

    List<ModeloEntity> listarActivos();

    List<ModeloEntity> listarTodos();

    List<ModeloEntity> buscar(String q);

    Optional<ModeloEntity> buscarPorId(Long id);

    ModeloEntity obtenerPorId(Long id);

    ModeloEntity guardar(ModeloEntity modelo);

    void cambiarEstado(Long id);
}
