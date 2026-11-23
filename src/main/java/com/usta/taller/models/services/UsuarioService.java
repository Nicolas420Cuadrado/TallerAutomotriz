package com.usta.taller.models.services;

import com.usta.taller.dtos.UsuarioForm;
import com.usta.taller.entities.RolEntity;
import com.usta.taller.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<UsuarioEntity> listarTodos();

    List<RolEntity> listarRoles();

    List<UsuarioEntity> listarActivos();

    List<UsuarioEntity> listarMecanicosActivos();

    Optional<UsuarioEntity> buscarPorId(Long id);

    UsuarioEntity obtenerPorId(Long id);

    UsuarioEntity guardar(UsuarioForm form);

    void cambiarEstado(Long id);

    long contarActivos();
}
