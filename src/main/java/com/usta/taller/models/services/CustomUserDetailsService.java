package com.usta.taller.models.services;

import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.models.daos.UsuarioDao;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioDao usuarioDao;

    public CustomUserDetailsService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioDao.findByEmailIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException("El usuario no tiene un rol asignado");
        }

        return User.withUsername(usuario.getEmail())
                .password(usuario.getClave())
                .disabled(!Boolean.TRUE.equals(usuario.getEstadoUsuario()))
                .roles(usuario.getRol().getNombreRol().name())
                .build();
    }
}
