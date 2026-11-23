package com.usta.taller.models.services;

import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.exceptions.ModeloDuplicadoException;
import com.usta.taller.exceptions.RecursoNoEncontradoException;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.daos.ModeloDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloServiceImp implements ModeloService {

    private final ModeloDao modeloDao;

    public ModeloServiceImp(ModeloDao modeloDao) {
        this.modeloDao = modeloDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeloEntity> listarActivos() {
        return modeloDao.findByEstadoModeloTrueOrderByMarcaAscNombreModeloAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeloEntity> listarTodos() {
        return modeloDao.findAllByOrderByMarcaAscNombreModeloAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeloEntity> buscar(String q) {
        if (q == null || q.isBlank()) {
            return listarActivos();
        }
        return modeloDao.buscarActivos(q.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModeloEntity> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return modeloDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ModeloEntity obtenerPorId(Long id) {
        return modeloDao.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El modelo solicitado no existe"));
    }

    @Override
    @Transactional
    public ModeloEntity guardar(ModeloEntity modelo) {
        if (modelo == null) {
            throw new ReglaNegocioException("Los datos del modelo son obligatorios");
        }

        String marca = modelo.getMarca() == null ? "" : modelo.getMarca().trim();
        String nombre = modelo.getNombreModelo() == null ? "" : modelo.getNombreModelo().trim();

        if (marca.isEmpty() || nombre.isEmpty()) {
            throw new ReglaNegocioException("La marca y el nombre del modelo son obligatorios");
        }

        modelo.setMarca(marca);
        modelo.setNombreModelo(nombre);

        if (modelo.getIdModelo() == null) {
            if (modeloDao.existsByMarcaIgnoreCaseAndNombreModeloIgnoreCase(marca, nombre)) {
                throw new ModeloDuplicadoException("Ya existe un modelo con esa marca y nombre");
            }
            modelo.setEstadoModelo(Boolean.TRUE);
        } else {
            ModeloEntity existente = obtenerPorId(modelo.getIdModelo());
            if (modeloDao.existsByMarcaIgnoreCaseAndNombreModeloIgnoreCaseAndIdModeloNot(
                    marca, nombre, modelo.getIdModelo())) {
                throw new ModeloDuplicadoException("Ya existe otro modelo con esa marca y nombre");
            }
            modelo.setEstadoModelo(existente.getEstadoModelo());
        }

        return modeloDao.save(modelo);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        ModeloEntity modelo = obtenerPorId(id);
        modelo.setEstadoModelo(!Boolean.TRUE.equals(modelo.getEstadoModelo()));
        modeloDao.save(modelo);
    }
}
