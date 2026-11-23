package com.usta.taller.services;

import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.exceptions.ModeloDuplicadoException;
import com.usta.taller.models.daos.ModeloDao;
import com.usta.taller.models.services.ModeloServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModeloServiceTest {

    @Mock
    private ModeloDao modeloDao;

    @InjectMocks
    private ModeloServiceImp modeloService;

    @Test
    void guardarRechazaMarcaYModeloDuplicados() {
        ModeloEntity modelo = new ModeloEntity();
        modelo.setMarca("Toyota");
        modelo.setNombreModelo("Corolla");

        when(modeloDao.existsByMarcaIgnoreCaseAndNombreModeloIgnoreCase("Toyota", "Corolla"))
                .thenReturn(true);

        assertThrows(ModeloDuplicadoException.class, () -> modeloService.guardar(modelo));
        verify(modeloDao, never()).save(any());
    }
}
