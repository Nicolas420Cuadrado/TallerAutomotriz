package com.usta.taller.controllers;

import com.usta.taller.dtos.VehiculoForm;
import com.usta.taller.entities.enums.ColorPlaca;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.ModeloService;
import com.usta.taller.models.services.ReparacionService;
import com.usta.taller.models.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final ModeloService modeloService;
    private final ReparacionService reparacionService;

    public VehiculoController(VehiculoService vehiculoService,
                              ModeloService modeloService,
                              ReparacionService reparacionService) {
        this.vehiculoService = vehiculoService;
        this.modeloService = modeloService;
        this.reparacionService = reparacionService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("vehiculos", vehiculoService.buscar(q));
        model.addAttribute("q", q);
        return "vehiculo/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("vehiculoForm", new VehiculoForm());
        model.addAttribute("editando", false);
        cargarCatalogos(model);
        return "vehiculo/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("vehiculoForm") VehiculoForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            cargarCatalogos(model);
            return "vehiculo/form";
        }
        try {
            vehiculoService.guardar(form);
            redirect.addFlashAttribute("exito", "Vehiculo registrado correctamente");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", false);
            cargarCatalogos(model);
            return "vehiculo/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("vehiculo", vehiculoService.obtenerPorId(id));
        return "vehiculo/detail";
    }

    @GetMapping("/{id}/reparaciones")
    public String reparaciones(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("vehiculo", vehiculoService.obtenerPorId(id));
        model.addAttribute("reparaciones", reparacionService.listarPorVehiculo(id, authentication.getName()));
        return "vehiculo/reparaciones";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("vehiculoForm", VehiculoForm.desde(vehiculoService.obtenerPorId(id)));
        model.addAttribute("editando", true);
        cargarCatalogos(model);
        return "vehiculo/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("vehiculoForm") VehiculoForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        form.setIdVehiculo(id);
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            cargarCatalogos(model);
            return "vehiculo/form";
        }
        try {
            vehiculoService.guardar(form);
            redirect.addFlashAttribute("exito", "Vehiculo actualizado correctamente");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", true);
            cargarCatalogos(model);
            return "vehiculo/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            vehiculoService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "Estado del vehiculo actualizado");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehiculos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("colores", ColorPlaca.values());
        model.addAttribute("modelos", modeloService.listarActivos());
    }
}
