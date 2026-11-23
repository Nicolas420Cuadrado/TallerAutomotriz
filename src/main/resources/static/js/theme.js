(function () {
    'use strict';

    var STORAGE_KEY = 'taller-automotriz-tema';
    var root = document.documentElement;

    function leerPreferenciaGuardada() {
        try {
            var valor = window.localStorage.getItem(STORAGE_KEY);
            return valor === 'light' || valor === 'dark' ? valor : null;
        } catch (error) {
            return null;
        }
    }

    function preferenciaDelSistema() {
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            return 'dark';
        }
        return 'light';
    }

    function temaActual() {
        return leerPreferenciaGuardada() || preferenciaDelSistema();
    }

    function aplicarTema(tema) {
        var etiqueta = tema === 'dark' ? 'Activar modo claro' : 'Activar modo oscuro';
        var botones = document.querySelectorAll('.theme-toggle');
        var i;

        root.setAttribute('data-theme', tema);

        for (i = 0; i < botones.length; i++) {
            botones[i].setAttribute('aria-label', etiqueta);
            botones[i].setAttribute('title', etiqueta);
            botones[i].setAttribute('aria-pressed', tema === 'dark' ? 'true' : 'false');
        }
    }

    function guardarTema(tema) {
        try {
            window.localStorage.setItem(STORAGE_KEY, tema);
        } catch (error) {
            return;
        }
    }

    function alternarTema() {
        var siguiente = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        aplicarTema(siguiente);
        guardarTema(siguiente);
    }

    aplicarTema(temaActual());

    document.addEventListener('click', function (evento) {
        var objetivo = evento.target;
        var boton = objetivo && objetivo.closest ? objetivo.closest('.theme-toggle') : null;

        if (boton) {
            evento.preventDefault();
            alternarTema();
        }
    });

    if (window.matchMedia) {
        var consulta = window.matchMedia('(prefers-color-scheme: dark)');

        function seguirSistema() {
            if (!leerPreferenciaGuardada()) {
                aplicarTema(preferenciaDelSistema());
            }
        }

        if (consulta.addEventListener) {
            consulta.addEventListener('change', seguirSistema);
        } else if (consulta.addListener) {
            consulta.addListener(seguirSistema);
        }
    }
}());
