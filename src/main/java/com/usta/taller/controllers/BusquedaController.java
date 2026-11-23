package com.usta.taller.controllers;

import com.usta.taller.entities.VehiculoEntity;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.ModeloService;
import com.usta.taller.models.services.RepuestoService;
import com.usta.taller.models.services.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/busquedas")
public class BusquedaController {

    private final ModeloService modeloService;
    private final RepuestoService repuestoService;
    private final VehiculoService vehiculoService;

    public BusquedaController(ModeloService modeloService,
                              RepuestoService repuestoService,
                              VehiculoService vehiculoService) {
        this.modeloService = modeloService;
        this.repuestoService = repuestoService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/repuestos-modelo")
    public String repuestosPorModeloForm(Model model) {
        model.addAttribute("modelos", modeloService.listarActivos());
        model.addAttribute("resultados", null);
        return "busqueda/repuestos-modelo";
    }

    @PostMapping("/repuestos-modelo")
    public String repuestosPorModelo(@RequestParam(value = "idModelo", required = false) Long idModelo,
                                     Model model) {
        model.addAttribute("modelos", modeloService.listarActivos());
        model.addAttribute("idModelo", idModelo);

        if (idModelo == null) {
            model.addAttribute("error", "Debe seleccionar un modelo");
            model.addAttribute("resultados", List.of());
            return "busqueda/repuestos-modelo";
        }

        try {
            model.addAttribute("modeloSeleccionado", modeloService.obtenerPorId(idModelo));
            model.addAttribute("resultados", repuestoService.buscarPorModelo(idModelo));
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("resultados", List.of());
        }
        return "busqueda/repuestos-modelo";
    }

    @GetMapping("/modelo-placa")
    public String modeloPorPlacaForm(Model model) {
        model.addAttribute("buscado", false);
        return "busqueda/modelo-placa";
    }

    @PostMapping("/modelo-placa")
    public String modeloPorPlaca(@RequestParam(value = "placa", required = false) String placa, Model model) {
        model.addAttribute("buscado", true);
        model.addAttribute("placa", placa);

        try {
            Optional<VehiculoEntity> vehiculo = vehiculoService.buscarPorPlaca(placa);
            if (vehiculo.isPresent()) {
                model.addAttribute("vehiculo", vehiculo.get());
            } else {
                model.addAttribute("error", "No se encontro un vehiculo con la placa indicada");
            }
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "busqueda/modelo-placa";
    }
}
