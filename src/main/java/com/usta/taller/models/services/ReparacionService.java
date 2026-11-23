package com.usta.taller.models.services;

import com.usta.taller.dtos.ReparacionForm;
import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.enums.EstadoReparacion;

import java.util.List;
import java.util.Optional;

public interface ReparacionService {

    List<ReparacionEntity> listarTodas();

    List<ReparacionEntity> listarParaUsuario(String correo);

    List<ReparacionEntity> listarPorVehiculo(Long idVehiculo, String correo);

    Optional<ReparacionEntity> buscarPorId(Long id);

    ReparacionEntity obtenerPorId(Long id);

    ReparacionEntity obtenerParaUsuario(Long id, String correo);

    ReparacionEntity guardar(ReparacionForm form, String correo);

    void cambiarEstado(Long id, EstadoReparacion nuevoEstado, String correo);

    long contarEnProceso();
}
