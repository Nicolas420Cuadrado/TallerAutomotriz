package com.usta.taller.models.services;

import com.usta.taller.dtos.RepuestoForm;
import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.entities.RepuestoEntity;
import com.usta.taller.exceptions.EanDuplicadoException;
import com.usta.taller.exceptions.RecursoNoEncontradoException;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.exceptions.RepuestoInvalidoException;
import com.usta.taller.models.daos.ModeloDao;
import com.usta.taller.models.daos.RepuestoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class RepuestoServiceImp implements RepuestoService {

    private static final Pattern PATRON_EAN = Pattern.compile("^\\d{13}$");

    private final RepuestoDao repuestoDao;
    private final ModeloDao modeloDao;

    public RepuestoServiceImp(RepuestoDao repuestoDao, ModeloDao modeloDao) {
        this.repuestoDao = repuestoDao;
        this.modeloDao = modeloDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> listarActivos() {
        return repuestoDao.findByEstadoRepuestoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> listarTodos() {
        return repuestoDao.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> buscar(String q) {
        if (q == null || q.isBlank()) {
            return listarActivos();
        }
        return repuestoDao.buscarActivos(q.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RepuestoEntity> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repuestoDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RepuestoEntity obtenerPorId(Long id) {
        return repuestoDao.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El repuesto solicitado no existe"));
    }

    @Override
    @Transactional
    public RepuestoEntity guardar(RepuestoForm form) {
        if (form == null) {
            throw new ReglaNegocioException("Los datos del repuesto son obligatorios");
        }

        String nombre = form.getNombre() == null ? "" : form.getNombre().trim();
        String ean = form.getEan() == null ? "" : form.getEan().trim();

        if (nombre.isEmpty()) {
            throw new RepuestoInvalidoException("El nombre del repuesto es obligatorio");
        }
        if (!PATRON_EAN.matcher(ean).matches()) {
            throw new RepuestoInvalidoException("El codigo EAN debe tener exactamente 13 digitos");
        }
        if (form.getPrecio() == null || form.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RepuestoInvalidoException("El precio debe ser mayor que cero");
        }
        if (form.getPrecio().precision() - form.getPrecio().scale() > 10 || form.getPrecio().scale() > 2) {
            throw new RepuestoInvalidoException("El precio debe tener maximo 10 enteros y 2 decimales");
        }
        if (form.getStock() == null || form.getStock() < 0) {
            throw new RepuestoInvalidoException("El stock no puede ser negativo");
        }
        if (form.getIdsModelos() == null || form.getIdsModelos().isEmpty()) {
            throw new RepuestoInvalidoException("Debe seleccionar al menos un modelo compatible");
        }

        if (form.getIdRepuesto() == null) {
            if (repuestoDao.existsByEan(ean)) {
                throw new EanDuplicadoException("Ya existe un repuesto con el codigo EAN " + ean);
            }
        } else {
            if (repuestoDao.existsByEanAndIdRepuestoNot(ean, form.getIdRepuesto())) {
                throw new EanDuplicadoException("Ya existe otro repuesto con el codigo EAN " + ean);
            }
        }

        Set<ModeloEntity> modelos = new HashSet<>();
        for (Long idModelo : form.getIdsModelos()) {
            ModeloEntity modelo = modeloDao.findById(idModelo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("El modelo " + idModelo + " no existe"));
            if (!Boolean.TRUE.equals(modelo.getEstadoModelo())) {
                throw new RepuestoInvalidoException("El modelo " + modelo.getDescripcion() + " esta inactivo");
            }
            modelos.add(modelo);
        }

        RepuestoEntity repuesto;
        if (form.getIdRepuesto() == null) {
            repuesto = new RepuestoEntity();
            repuesto.setEstadoRepuesto(Boolean.TRUE);
        } else {
            repuesto = obtenerPorId(form.getIdRepuesto());
        }

        repuesto.setNombre(nombre);
        repuesto.setDescripcion(textoONulo(form.getDescripcion()));
        repuesto.setPrecio(form.getPrecio());
        repuesto.setEan(ean);
        repuesto.setStock(form.getStock());
        repuesto.setImagenUrl(textoONulo(form.getImagenUrl()));
        repuesto.setModelos(modelos);

        return repuestoDao.save(repuesto);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        RepuestoEntity repuesto = obtenerPorId(id);
        repuesto.setEstadoRepuesto(!Boolean.TRUE.equals(repuesto.getEstadoRepuesto()));
        repuestoDao.save(repuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> buscarPorModelo(Long idModelo) {
        if (idModelo == null) {
            throw new ReglaNegocioException("Debe seleccionar un modelo");
        }
        modeloDao.findById(idModelo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El modelo seleccionado no existe"));
        return repuestoDao.buscarActivosPorModelo(idModelo);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return repuestoDao.countByEstadoRepuestoTrue();
    }

    private String textoONulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
