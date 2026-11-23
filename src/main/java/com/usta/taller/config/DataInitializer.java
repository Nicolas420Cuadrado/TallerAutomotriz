package com.usta.taller.config;

import com.usta.taller.entities.RolEntity;
import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.entities.enums.NombreRol;
import com.usta.taller.models.daos.RolDao;
import com.usta.taller.models.daos.UsuarioDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolDao rolDao;
    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolDao rolDao, UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.rolDao = rolDao;
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        RolEntity rolAdministrador = obtenerOCrearRol(NombreRol.ADMINISTRADOR);
        RolEntity rolMecanico = obtenerOCrearRol(NombreRol.MECANICO);

        if (!usuarioDao.existsByEmailIgnoreCase("admin@taller.com")) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Administrador General");
            admin.setDocumento("1000000001");
            admin.setEmail("admin@taller.com");
            admin.setClave(passwordEncoder.encode("admin123"));
            admin.setEstadoUsuario(Boolean.TRUE);
            admin.setRol(rolAdministrador);
            usuarioDao.save(admin);
        }

        if (!usuarioDao.existsByEmailIgnoreCase("mecanico@taller.com")) {
            UsuarioEntity mecanico = new UsuarioEntity();
            mecanico.setNombre("Mecanico Demo");
            mecanico.setDocumento("1000000002");
            mecanico.setEmail("mecanico@taller.com");
            mecanico.setClave(passwordEncoder.encode("mecanico123"));
            mecanico.setEstadoUsuario(Boolean.TRUE);
            mecanico.setRol(rolMecanico);
            usuarioDao.save(mecanico);
        }
    }

    private RolEntity obtenerOCrearRol(NombreRol nombreRol) {
        return rolDao.findByNombreRol(nombreRol)
                .orElseGet(() -> rolDao.save(new RolEntity(nombreRol)));
    }
}
