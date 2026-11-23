package com.usta.taller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorVistasController {

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/403";
    }
}
