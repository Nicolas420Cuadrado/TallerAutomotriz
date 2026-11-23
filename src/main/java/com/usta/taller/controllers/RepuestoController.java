package com.usta.taller.controllers;

import com.usta.taller.dtos.RepuestoForm;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.ModeloService;
import com.usta.taller.models.services.RepuestoService;
import jakarta.validation.Valid;
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
@RequestMapping("/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;
    private final ModeloService modeloService;

    public RepuestoController(RepuestoService repuestoService, ModeloService modeloService) {
        this.repuestoService = repuestoService;
        this.modeloService = modeloService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("repuestos", repuestoService.buscar(q));
        model.addAttribute("q", q);
        return "repuesto/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("repuestoForm", new RepuestoForm());
        model.addAttribute("editando", false);
        cargarModelos(model);
        return "repuesto/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("repuestoForm") RepuestoForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            cargarModelos(model);
            return "repuesto/form";
        }
        try {
            repuestoService.guardar(form);
            redirect.addFlashAttribute("exito", "Repuesto registrado correctamente");
            return "redirect:/repuestos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", false);
            cargarModelos(model);
            return "repuesto/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("repuesto", repuestoService.obtenerPorId(id));
        return "repuesto/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("repuestoForm", RepuestoForm.desde(repuestoService.obtenerPorId(id)));
        model.addAttribute("editando", true);
        cargarModelos(model);
        return "repuesto/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("repuestoForm") RepuestoForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        form.setIdRepuesto(id);
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            cargarModelos(model);
            return "repuesto/form";
        }
        try {
            repuestoService.guardar(form);
            redirect.addFlashAttribute("exito", "Repuesto actualizado correctamente");
            return "redirect:/repuestos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", true);
            cargarModelos(model);
            return "repuesto/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            repuestoService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "Estado del repuesto actualizado");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/repuestos";
    }

    private void cargarModelos(Model model) {
        model.addAttribute("modelos", modeloService.listarActivos());
    }
}
