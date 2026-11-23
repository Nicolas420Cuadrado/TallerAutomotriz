package com.usta.taller.services;

import com.usta.taller.dtos.RepuestoForm;
import com.usta.taller.exceptions.RepuestoInvalidoException;
import com.usta.taller.models.daos.ModeloDao;
import com.usta.taller.models.daos.RepuestoDao;
import com.usta.taller.models.services.RepuestoServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepuestoServiceTest {

    @Mock
    private RepuestoDao repuestoDao;

    @Mock
    private ModeloDao modeloDao;

    @InjectMocks
    private RepuestoServiceImp repuestoService;

    private RepuestoForm formValido() {
        RepuestoForm form = new RepuestoForm();
        form.setNombre("Filtro de aceite");
        form.setPrecio(new BigDecimal("35000.00"));
        form.setEan("1234567890123");
        form.setStock(10);
        form.setIdsModelos(Set.of(1L));
        return form;
    }

    @Test
    void guardarRechazaEanConLongitudIncorrecta() {
        RepuestoForm form = formValido();
        form.setEan("123");

        RepuestoInvalidoException ex = assertThrows(RepuestoInvalidoException.class,
                () -> repuestoService.guardar(form));

        assertTrue(ex.getMessage().contains("13 digitos"));
        verify(repuestoDao, never()).save(any());
    }

    @Test
    void guardarRechazaPrecioMenorOIgualACero() {
        RepuestoForm form = formValido();
        form.setPrecio(BigDecimal.ZERO);

        assertThrows(RepuestoInvalidoException.class, () -> repuestoService.guardar(form));
        verify(repuestoDao, never()).save(any());
    }

    @Test
    void guardarRechazaStockNegativo() {
        RepuestoForm form = formValido();
        form.setStock(-1);

        assertThrows(RepuestoInvalidoException.class, () -> repuestoService.guardar(form));
        verify(repuestoDao, never()).save(any());
    }

    @Test
    void guardarRechazaPrecioConMasDeDosDecimales() {
        RepuestoForm form = formValido();
        form.setPrecio(new BigDecimal("10.123"));

        assertThrows(RepuestoInvalidoException.class, () -> repuestoService.guardar(form));
        verify(repuestoDao, never()).save(any());
    }

    @Test
    void guardarRechazaPrecioQueSuperaLaPrecisionDeLaColumna() {
        RepuestoForm form = formValido();
        form.setPrecio(new BigDecimal("10000000000.00"));

        assertThrows(RepuestoInvalidoException.class, () -> repuestoService.guardar(form));
        verify(repuestoDao, never()).save(any());
    }
}
