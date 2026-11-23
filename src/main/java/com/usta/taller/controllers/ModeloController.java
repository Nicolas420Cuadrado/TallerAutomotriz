package com.usta.taller.controllers;

import com.usta.taller.entities.ModeloEntity;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.ModeloService;
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
@RequestMapping("/modelos")
public class ModeloController {

    private final ModeloService modeloService;

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("modelos", modeloService.buscar(q));
        model.addAttribute("q", q);
        return "modelo/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("modelo", new ModeloEntity());
        model.addAttribute("editando", false);
        return "modelo/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("modelo") ModeloEntity modelo,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            return "modelo/form";
        }
        try {
            modeloService.guardar(modelo);
            redirect.addFlashAttribute("exito", "Modelo registrado correctamente");
            return "redirect:/modelos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", false);
            return "modelo/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("modelo", modeloService.obtenerPorId(id));
        return "modelo/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("modelo", modeloService.obtenerPorId(id));
        model.addAttribute("editando", true);
        return "modelo/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("modelo") ModeloEntity modelo,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        modelo.setIdModelo(id);
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            return "modelo/form";
        }
        try {
            modeloService.guardar(modelo);
            redirect.addFlashAttribute("exito", "Modelo actualizado correctamente");
            return "redirect:/modelos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", true);
            return "modelo/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            modeloService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "Estado del modelo actualizado");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/modelos";
    }
}
