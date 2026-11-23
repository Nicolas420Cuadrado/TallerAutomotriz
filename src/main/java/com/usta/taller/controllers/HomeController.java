package com.usta.taller.controllers;

import com.usta.taller.models.services.ReparacionService;
import com.usta.taller.models.services.RepuestoService;
import com.usta.taller.models.services.UsuarioService;
import com.usta.taller.models.services.VehiculoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final VehiculoService vehiculoService;
    private final RepuestoService repuestoService;
    private final ReparacionService reparacionService;
    private final UsuarioService usuarioService;

    public HomeController(VehiculoService vehiculoService,
                          RepuestoService repuestoService,
                          ReparacionService reparacionService,
                          UsuarioService usuarioService) {
        this.vehiculoService = vehiculoService;
        this.repuestoService = repuestoService;
        this.reparacionService = reparacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("vehiculosActivos", vehiculoService.contarActivos());
        model.addAttribute("repuestosActivos", repuestoService.contarActivos());
        model.addAttribute("reparacionesEnProceso", reparacionService.contarEnProceso());
        model.addAttribute("usuariosActivos", usuarioService.contarActivos());
        model.addAttribute("reparacionesRecientes",
                reparacionService.listarParaUsuario(authentication.getName()).stream().limit(5).toList());
        return "home/dashboard";
    }
}
