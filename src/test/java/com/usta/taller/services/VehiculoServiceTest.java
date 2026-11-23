package com.usta.taller.services;

import com.usta.taller.dtos.VehiculoForm;
import com.usta.taller.entities.enums.ColorPlaca;
import com.usta.taller.exceptions.PlacaDuplicadaException;
import com.usta.taller.exceptions.PlacaInvalidaException;
import com.usta.taller.models.daos.ModeloDao;
import com.usta.taller.models.daos.VehiculoDao;
import com.usta.taller.models.services.VehiculoServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoDao vehiculoDao;

    @Mock
    private ModeloDao modeloDao;

    @InjectMocks
    private VehiculoServiceImp vehiculoService;

    private VehiculoForm formValido() {
        VehiculoForm form = new VehiculoForm();
        form.setPlaca("ABC-123");
        form.setColorPlaca(ColorPlaca.AMARILLA);
        form.setNombreDueno("Juan Perez");
        form.setDocumentoDueno("10203040");
        form.setTelefonoDueno("3001234567");
        form.setIdModelo(1L);
        return form;
    }

    @Test
    void guardarRechazaPlacaConFormatoInvalido() {
        VehiculoForm form = formValido();
        form.setPlaca("abc123");

        PlacaInvalidaException ex = assertThrows(PlacaInvalidaException.class,
                () -> vehiculoService.guardar(form));

        assertTrue(ex.getMessage().contains("ABC-123"));
        verify(vehiculoDao, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void guardarRechazaPlacaDuplicada() {
        VehiculoForm form = formValido();
        when(vehiculoDao.existsByPlacaIgnoreCase("ABC-123")).thenReturn(true);

        assertThrows(PlacaDuplicadaException.class, () -> vehiculoService.guardar(form));
        verify(vehiculoDao, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
