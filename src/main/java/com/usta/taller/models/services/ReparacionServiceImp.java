package com.usta.taller.models.services;

import com.usta.taller.dtos.ReparacionForm;
import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.entities.VehiculoEntity;
import com.usta.taller.entities.enums.EstadoReparacion;
import com.usta.taller.entities.enums.NombreRol;
import com.usta.taller.exceptions.AccesoNoAutorizadoException;
import com.usta.taller.exceptions.RecursoNoEncontradoException;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.exceptions.ReparacionInvalidaException;
import com.usta.taller.models.daos.ReparacionDao;
import com.usta.taller.models.daos.UsuarioDao;
import com.usta.taller.models.daos.VehiculoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReparacionServiceImp implements ReparacionService {

    private final ReparacionDao reparacionDao;
    private final VehiculoDao vehiculoDao;
    private final UsuarioDao usuarioDao;

    public ReparacionServiceImp(ReparacionDao reparacionDao, VehiculoDao vehiculoDao, UsuarioDao usuarioDao) {
        this.reparacionDao = reparacionDao;
        this.vehiculoDao = vehiculoDao;
        this.usuarioDao = usuarioDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReparacionEntity> listarTodas() {
        return reparacionDao.findAllByOrderByFechaDescIdReparacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReparacionEntity> listarParaUsuario(String correo) {
        UsuarioEntity usuario = obtenerUsuario(correo);
        if (esAdministrador(usuario)) {
            return listarTodas();
        }
        return reparacionDao.findByMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(usuario.getIdUsuario());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReparacionEntity> listarPorVehiculo(Long idVehiculo, String correo) {
        if (idVehiculo == null) {
            throw new RecursoNoEncontradoException("El vehiculo solicitado no existe");
        }
        vehiculoDao.findById(idVehiculo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El vehiculo solicitado no existe"));

        UsuarioEntity usuario = obtenerUsuario(correo);
        if (esAdministrador(usuario)) {
            return reparacionDao.findByVehiculo_IdVehiculoOrderByFechaDescIdReparacionDesc(idVehiculo);
        }
        return reparacionDao.findByVehiculo_IdVehiculoAndMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(
                idVehiculo, usuario.getIdUsuario());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReparacionEntity> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return reparacionDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReparacionEntity obtenerPorId(Long id) {
        return reparacionDao.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reparacion solicitada no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public ReparacionEntity obtenerParaUsuario(Long id, String correo) {
        ReparacionEntity reparacion = obtenerPorId(id);
        UsuarioEntity usuario = obtenerUsuario(correo);
        validarAcceso(reparacion, usuario);
        return reparacion;
    }

    @Override
    @Transactional
    public ReparacionEntity guardar(ReparacionForm form, String correo) {
        if (form == null) {
            throw new ReparacionInvalidaException("Los datos de la reparacion son obligatorios");
        }

        UsuarioEntity usuario = obtenerUsuario(correo);
        if (!Boolean.TRUE.equals(usuario.getEstadoUsuario())) {
            throw new AccesoNoAutorizadoException("El usuario se encuentra inactivo");
        }

        if (form.getTipo() == null) {
            throw new ReparacionInvalidaException("El tipo de reparacion es obligatorio");
        }

        VehiculoEntity vehiculo = vehiculoDao.findById(form.getIdVehiculo())
                .orElseThrow(() -> new RecursoNoEncontradoException("El vehiculo seleccionado no existe"));
        if (!Boolean.TRUE.equals(vehiculo.getEstadoVehiculo())) {
            throw new ReparacionInvalidaException("El vehiculo seleccionado esta inactivo");
        }

        UsuarioEntity mecanico;
        if (esAdministrador(usuario)) {
            if (form.getIdMecanico() == null) {
                throw new ReparacionInvalidaException("Debe seleccionar un mecanico");
            }
            mecanico = usuarioDao.findById(form.getIdMecanico())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El mecanico seleccionado no existe"));
            if (!Boolean.TRUE.equals(mecanico.getEstadoUsuario())) {
                throw new ReparacionInvalidaException("El mecanico seleccionado esta inactivo");
            }
            if (mecanico.getRol() == null || mecanico.getRol().getNombreRol() != NombreRol.MECANICO) {
                throw new ReparacionInvalidaException("El usuario seleccionado no tiene rol de mecanico");
            }
        } else {
            mecanico = usuario;
        }

        ReparacionEntity reparacion;
        if (form.getIdReparacion() == null) {
            reparacion = new ReparacionEntity();
            reparacion.setEstado(EstadoReparacion.EN_PROCESO);
        } else {
            reparacion = obtenerPorId(form.getIdReparacion());
            validarAcceso(reparacion, usuario);
            if (reparacion.getEstado() != EstadoReparacion.EN_PROCESO) {
                throw new ReparacionInvalidaException("Solo se pueden editar reparaciones en proceso");
            }
        }

        reparacion.setFecha(form.getFecha() == null ? LocalDate.now() : form.getFecha());
        reparacion.setTipo(form.getTipo());
        reparacion.setVehiculo(vehiculo);
        reparacion.setMecanico(mecanico);
        reparacion.setObservaciones(textoONulo(form.getObservaciones()));

        return reparacionDao.save(reparacion);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, EstadoReparacion nuevoEstado, String correo) {
        ReparacionEntity reparacion = obtenerPorId(id);
        UsuarioEntity usuario = obtenerUsuario(correo);
        validarAcceso(reparacion, usuario);

        if (reparacion.getEstado() != EstadoReparacion.EN_PROCESO) {
            throw new ReparacionInvalidaException("Solo se puede cambiar el estado de una reparacion en proceso");
        }
        if (nuevoEstado == null || nuevoEstado == EstadoReparacion.EN_PROCESO) {
            throw new ReparacionInvalidaException("El nuevo estado debe ser TERMINADO o CANCELADO");
        }

        reparacion.setEstado(nuevoEstado);
        reparacionDao.save(reparacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEnProceso() {
        return reparacionDao.countByEstado(EstadoReparacion.EN_PROCESO);
    }

    private UsuarioEntity obtenerUsuario(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new AccesoNoAutorizadoException("No hay un usuario autenticado");
        }
        return usuarioDao.findByEmailIgnoreCase(correo)
                .orElseThrow(() -> new AccesoNoAutorizadoException("El usuario autenticado no existe"));
    }

    private boolean esAdministrador(UsuarioEntity usuario) {
        return usuario.getRol() != null && usuario.getRol().getNombreRol() == NombreRol.ADMINISTRADOR;
    }

    private void validarAcceso(ReparacionEntity reparacion, UsuarioEntity usuario) {
        if (esAdministrador(usuario)) {
            return;
        }
        if (reparacion.getMecanico() == null
                || !reparacion.getMecanico().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new AccesoNoAutorizadoException("No tiene permiso para acceder a esta reparacion");
        }
    }

    private String textoONulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
