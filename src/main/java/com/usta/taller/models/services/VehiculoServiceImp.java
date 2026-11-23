package com.usta.taller.models.services;

import com.usta.taller.dtos.VehiculoForm;
import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.entities.VehiculoEntity;
import com.usta.taller.exceptions.PlacaDuplicadaException;
import com.usta.taller.exceptions.PlacaInvalidaException;
import com.usta.taller.exceptions.RecursoNoEncontradoException;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.daos.ModeloDao;
import com.usta.taller.models.daos.VehiculoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VehiculoServiceImp implements VehiculoService {

    private static final Pattern PATRON_PLACA = Pattern.compile("^[A-Z]{3}-[0-9]{3}$");

    private final VehiculoDao vehiculoDao;
    private final ModeloDao modeloDao;

    public VehiculoServiceImp(VehiculoDao vehiculoDao, ModeloDao modeloDao) {
        this.vehiculoDao = vehiculoDao;
        this.modeloDao = modeloDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoEntity> listarActivos() {
        return vehiculoDao.findByEstadoVehiculoTrueOrderByPlacaAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoEntity> listarTodos() {
        return vehiculoDao.findAllByOrderByPlacaAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoEntity> buscar(String q) {
        if (q == null || q.isBlank()) {
            return listarActivos();
        }
        return vehiculoDao.buscarActivos(q.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehiculoEntity> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return vehiculoDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoEntity obtenerPorId(Long id) {
        return vehiculoDao.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El vehiculo solicitado no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehiculoEntity> buscarPorPlaca(String placa) {
        String normalizada = placa == null ? "" : placa.trim().toUpperCase();
        if (!PATRON_PLACA.matcher(normalizada).matches()) {
            throw new PlacaInvalidaException("La placa debe tener el formato ABC-123");
        }
        return vehiculoDao.findByPlacaIgnoreCase(normalizada);
    }

    @Override
    @Transactional
    public VehiculoEntity guardar(VehiculoForm form) {
        if (form == null) {
            throw new ReglaNegocioException("Los datos del vehiculo son obligatorios");
        }

        String placa = form.getPlaca() == null ? "" : form.getPlaca().trim().toUpperCase();
        if (!PATRON_PLACA.matcher(placa).matches()) {
            throw new PlacaInvalidaException("La placa debe tener el formato ABC-123");
        }

        if (form.getIdVehiculo() == null) {
            if (vehiculoDao.existsByPlacaIgnoreCase(placa)) {
                throw new PlacaDuplicadaException("Ya existe un vehiculo con la placa " + placa);
            }
        } else {
            if (vehiculoDao.existsByPlacaIgnoreCaseAndIdVehiculoNot(placa, form.getIdVehiculo())) {
                throw new PlacaDuplicadaException("Ya existe otro vehiculo con la placa " + placa);
            }
        }

        if (form.getIdModelo() == null) {
            throw new ReglaNegocioException("El modelo es obligatorio");
        }

        ModeloEntity modelo = modeloDao.findById(form.getIdModelo())
                .orElseThrow(() -> new RecursoNoEncontradoException("El modelo seleccionado no existe"));

        if (!Boolean.TRUE.equals(modelo.getEstadoModelo())) {
            throw new ReglaNegocioException("El modelo seleccionado esta inactivo");
        }

        VehiculoEntity vehiculo;
        if (form.getIdVehiculo() == null) {
            vehiculo = new VehiculoEntity();
            vehiculo.setEstadoVehiculo(Boolean.TRUE);
        } else {
            vehiculo = obtenerPorId(form.getIdVehiculo());
        }

        vehiculo.setPlaca(placa);
        vehiculo.setColorPlaca(form.getColorPlaca());
        vehiculo.setNombreDueno(form.getNombreDueno().trim());
        vehiculo.setDocumentoDueno(form.getDocumentoDueno().trim());
        vehiculo.setTelefonoDueno(form.getTelefonoDueno().trim());
        vehiculo.setModelo(modelo);

        return vehiculoDao.save(vehiculo);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        VehiculoEntity vehiculo = obtenerPorId(id);
        vehiculo.setEstadoVehiculo(!Boolean.TRUE.equals(vehiculo.getEstadoVehiculo()));
        vehiculoDao.save(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return vehiculoDao.countByEstadoVehiculoTrue();
    }
}
