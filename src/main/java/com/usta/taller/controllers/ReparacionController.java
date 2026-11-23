package com.usta.taller.controllers;

import com.usta.taller.dtos.ReparacionForm;
import com.usta.taller.entities.ReparacionEntity;
import com.usta.taller.entities.enums.EstadoReparacion;
import com.usta.taller.entities.enums.TipoReparacion;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.ReparacionService;
import com.usta.taller.models.services.UsuarioService;
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
@RequestMapping("/reparaciones")
public class ReparacionController {

    private final ReparacionService reparacionService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;

    public ReparacionController(ReparacionService reparacionService,
                                VehiculoService vehiculoService,
                                UsuarioService usuarioService) {
        this.reparacionService = reparacionService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("reparaciones", reparacionService.listarParaUsuario(authentication.getName()));
        model.addAttribute("estados", EstadoReparacion.values());
        return "reparacion/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("reparacionForm", new ReparacionForm());
        model.addAttribute("editando", false);
        cargarCatalogos(model);
        return "reparacion/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("reparacionForm") ReparacionForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect,
                        Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            cargarCatalogos(model);
            return "reparacion/form";
        }
        try {
            reparacionService.guardar(form, authentication.getName());
            redirect.addFlashAttribute("exito", "Reparacion registrada correctamente");
            return "redirect:/reparaciones";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", false);
            cargarCatalogos(model);
            return "reparacion/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("reparacion", reparacionService.obtenerParaUsuario(id, authentication.getName()));
        model.addAttribute("estados", EstadoReparacion.values());
        return "reparacion/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         Model model,
                         Authentication authentication,
                         RedirectAttributes redirect) {
        ReparacionEntity reparacion = reparacionService.obtenerParaUsuario(id, authentication.getName());
        if (reparacion.getEstado() != EstadoReparacion.EN_PROCESO) {
            redirect.addFlashAttribute("error", "Solo se pueden editar reparaciones en proceso");
            return "redirect:/reparaciones";
        }
        model.addAttribute("reparacionForm", ReparacionForm.desde(reparacion));
        model.addAttribute("editando", true);
        cargarCatalogos(model);
        return "reparacion/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("reparacionForm") ReparacionForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect,
                             Authentication authentication) {
        form.setIdReparacion(id);
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            cargarCatalogos(model);
            return "reparacion/form";
        }
        try {
            reparacionService.guardar(form, authentication.getName());
            redirect.addFlashAttribute("exito", "Reparacion actualizada correctamente");
            return "redirect:/reparaciones";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", true);
            cargarCatalogos(model);
            return "reparacion/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam(value = "estado", required = false) EstadoReparacion estado,
                                RedirectAttributes redirect,
                                Authentication authentication) {
        try {
            reparacionService.cambiarEstado(id, estado, authentication.getName());
            redirect.addFlashAttribute("exito", "Estado de la reparacion actualizado");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reparaciones";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("vehiculos", vehiculoService.listarActivos());
        model.addAttribute("mecanicos", usuarioService.listarMecanicosActivos());
        model.addAttribute("tipos", TipoReparacion.values());
        model.addAttribute("estados", EstadoReparacion.values());
    }
}
