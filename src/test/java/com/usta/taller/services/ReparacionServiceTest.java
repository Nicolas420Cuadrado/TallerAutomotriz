package com.usta.taller.services;

import com.usta.taller.dtos.ReparacionForm;
import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.RolEntity;
import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.entities.VehiculoEntity;
import com.usta.taller.entities.enums.EstadoReparacion;
import com.usta.taller.entities.enums.NombreRol;
import com.usta.taller.entities.enums.TipoReparacion;
import com.usta.taller.exceptions.AccesoNoAutorizadoException;
import com.usta.taller.exceptions.ReparacionInvalidaException;
import com.usta.taller.models.daos.ReparacionDao;
import com.usta.taller.models.daos.UsuarioDao;
import com.usta.taller.models.daos.VehiculoDao;
import com.usta.taller.models.services.ReparacionServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReparacionServiceTest {

    @Mock
    private ReparacionDao reparacionDao;

    @Mock
    private VehiculoDao vehiculoDao;

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    private ReparacionServiceImp reparacionService;

    private UsuarioEntity mecanico(Long id, String email) {
        RolEntity rol = new RolEntity(NombreRol.MECANICO);
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(id);
        usuario.setEmail(email);
        usuario.setEstadoUsuario(Boolean.TRUE);
        usuario.setRol(rol);
        return usuario;
    }

    private UsuarioEntity administrador(Long id, String email) {
        RolEntity rol = new RolEntity(NombreRol.ADMINISTRADOR);
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(id);
        usuario.setEmail(email);
        usuario.setEstadoUsuario(Boolean.TRUE);
        usuario.setRol(rol);
        return usuario;
    }

    private VehiculoEntity vehiculoActivo() {
        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setIdVehiculo(1L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setEstadoVehiculo(Boolean.TRUE);
        return vehiculo;
    }

    private ReparacionForm formValido() {
        ReparacionForm form = new ReparacionForm();
        form.setFecha(LocalDate.of(2026, 11, 2));
        form.setTipo(TipoReparacion.MANTENIMIENTO_PREVENTIVO);
        form.setIdVehiculo(1L);
        form.setObservaciones("Cambio de aceite");
        return form;
    }

    @Test
    void guardarCreaReparacionEnProcesoAsignadaAlMecanicoAutenticado() {
        UsuarioEntity mecanico = mecanico(2L, "mecanico@taller.com");
        when(usuarioDao.findByEmailIgnoreCase("mecanico@taller.com")).thenReturn(Optional.of(mecanico));
        when(vehiculoDao.findById(1L)).thenReturn(Optional.of(vehiculoActivo()));
        when(reparacionDao.save(any(ReparacionEntity.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        ReparacionEntity reparacion = reparacionService.guardar(formValido(), "mecanico@taller.com");

        assertNotNull(reparacion);
        assertEquals(EstadoReparacion.EN_PROCESO, reparacion.getEstado());
        assertSame(mecanico, reparacion.getMecanico());
        assertEquals(TipoReparacion.MANTENIMIENTO_PREVENTIVO, reparacion.getTipo());
    }

    @Test
    void cambiarEstadoRechazaReparacionQueYaEstaTerminada() {
        UsuarioEntity mecanico = mecanico(2L, "mecanico@taller.com");
        ReparacionEntity reparacion = new ReparacionEntity();
        reparacion.setIdReparacion(1L);
        reparacion.setEstado(EstadoReparacion.TERMINADO);
        reparacion.setMecanico(mecanico);

        when(usuarioDao.findByEmailIgnoreCase("mecanico@taller.com")).thenReturn(Optional.of(mecanico));
        when(reparacionDao.findById(1L)).thenReturn(Optional.of(reparacion));

        assertThrows(ReparacionInvalidaException.class,
                () -> reparacionService.cambiarEstado(1L, EstadoReparacion.CANCELADO, "mecanico@taller.com"));
    }

    @Test
    void mecanicoNoPuedeEditarReparacionDeOtroMecanico() {
        UsuarioEntity mecanicoActual = mecanico(2L, "mecanico@taller.com");
        UsuarioEntity otroMecanico = mecanico(99L, "otro@taller.com");

        ReparacionEntity reparacionAjena = new ReparacionEntity();
        reparacionAjena.setIdReparacion(1L);
        reparacionAjena.setEstado(EstadoReparacion.EN_PROCESO);
        reparacionAjena.setMecanico(otroMecanico);

        ReparacionForm form = formValido();
        form.setIdReparacion(1L);

        when(usuarioDao.findByEmailIgnoreCase("mecanico@taller.com")).thenReturn(Optional.of(mecanicoActual));
        when(vehiculoDao.findById(1L)).thenReturn(Optional.of(vehiculoActivo()));
        when(reparacionDao.findById(1L)).thenReturn(Optional.of(reparacionAjena));

        assertThrows(AccesoNoAutorizadoException.class,
                () -> reparacionService.guardar(form, "mecanico@taller.com"));
    }

    @Test
    void administradorConsultaElHistorialCompletoDelVehiculo() {
        UsuarioEntity administrador = administrador(1L, "admin@taller.com");
        List<ReparacionEntity> historial = List.of(new ReparacionEntity());

        when(usuarioDao.findByEmailIgnoreCase("admin@taller.com")).thenReturn(Optional.of(administrador));
        when(vehiculoDao.findById(1L)).thenReturn(Optional.of(vehiculoActivo()));
        when(reparacionDao.findByVehiculo_IdVehiculoOrderByFechaDescIdReparacionDesc(1L))
                .thenReturn(historial);

        List<ReparacionEntity> resultado = reparacionService.listarPorVehiculo(1L, "admin@taller.com");

        assertSame(historial, resultado);
        verify(reparacionDao).findByVehiculo_IdVehiculoOrderByFechaDescIdReparacionDesc(1L);
    }

    @Test
    void mecanicoSoloConsultaElHistorialDeSusReparacionesAsignadas() {
        UsuarioEntity mecanico = mecanico(2L, "mecanico@taller.com");
        List<ReparacionEntity> historial = List.of(new ReparacionEntity());

        when(usuarioDao.findByEmailIgnoreCase("mecanico@taller.com")).thenReturn(Optional.of(mecanico));
        when(vehiculoDao.findById(1L)).thenReturn(Optional.of(vehiculoActivo()));
        when(reparacionDao.findByVehiculo_IdVehiculoAndMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(1L, 2L))
                .thenReturn(historial);

        List<ReparacionEntity> resultado = reparacionService.listarPorVehiculo(1L, "mecanico@taller.com");

        assertSame(historial, resultado);
        verify(reparacionDao)
                .findByVehiculo_IdVehiculoAndMecanico_IdUsuarioOrderByFechaDescIdReparacionDesc(1L, 2L);
    }
}
