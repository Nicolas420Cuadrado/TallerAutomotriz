package com.usta.taller.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public String accesoDenegado(AccesoNoAutorizadoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String noEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    public String errorGeneral(Exception ex, Model model) {
        model.addAttribute("mensaje", "Ocurrio un error inesperado. Intente nuevamente.");
        return "error/500";
    }
}
