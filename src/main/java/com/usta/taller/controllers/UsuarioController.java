package com.usta.taller.controllers;

import com.usta.taller.dtos.UsuarioForm;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.models.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        model.addAttribute("editando", false);
        model.addAttribute("roles", usuarioService.listarRoles());
        return "usuario/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            model.addAttribute("roles", usuarioService.listarRoles());
            return "usuario/form";
        }
        try {
            usuarioService.guardar(form);
            redirect.addFlashAttribute("exito", "Usuario registrado correctamente");
            return "redirect:/usuarios";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", false);
            model.addAttribute("roles", usuarioService.listarRoles());
            return "usuario/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.obtenerPorId(id));
        return "usuario/detail";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarioForm", UsuarioForm.desde(usuarioService.obtenerPorId(id)));
        model.addAttribute("editando", true);
        model.addAttribute("roles", usuarioService.listarRoles());
        return "usuario/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        form.setIdUsuario(id);
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            model.addAttribute("roles", usuarioService.listarRoles());
            return "usuario/form";
        }
        try {
            usuarioService.guardar(form);
            redirect.addFlashAttribute("exito", "Usuario actualizado correctamente");
            return "redirect:/usuarios";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("editando", true);
            model.addAttribute("roles", usuarioService.listarRoles());
            return "usuario/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarioService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "Estado del usuario actualizado");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}
