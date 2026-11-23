package com.usta.taller.models.services;

import com.usta.taller.dtos.UsuarioForm;
import com.usta.taller.entities.RolEntity;
import com.usta.taller.entities.UsuarioEntity;
import com.usta.taller.entities.enums.NombreRol;
import com.usta.taller.exceptions.RecursoNoEncontradoException;
import com.usta.taller.exceptions.ReglaNegocioException;
import com.usta.taller.exceptions.UsuarioDuplicadoException;
import com.usta.taller.models.daos.RolDao;
import com.usta.taller.models.daos.UsuarioDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImp implements UsuarioService {

    private final UsuarioDao usuarioDao;
    private final RolDao rolDao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImp(UsuarioDao usuarioDao, RolDao rolDao, PasswordEncoder passwordEncoder) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioEntity> listarTodos() {
        return usuarioDao.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolEntity> listarRoles() {
        return rolDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioEntity> listarActivos() {
        return usuarioDao.findByEstadoUsuarioTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioEntity> listarMecanicosActivos() {
        return usuarioDao.findByEstadoUsuarioTrueAndRol_NombreRolOrderByNombreAsc(NombreRol.MECANICO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioEntity> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return usuarioDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEntity obtenerPorId(Long id) {
        return usuarioDao.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario solicitado no existe"));
    }

    @Override
    @Transactional
    public UsuarioEntity guardar(UsuarioForm form) {
        if (form == null) {
            throw new ReglaNegocioException("Los datos del usuario son obligatorios");
        }

        String nombre = form.getNombre() == null ? "" : form.getNombre().trim();
        String documento = form.getDocumento() == null ? "" : form.getDocumento().trim();
        String email = form.getEmail() == null ? "" : form.getEmail().trim().toLowerCase();

        if (nombre.isEmpty() || documento.isEmpty() || email.isEmpty()) {
            throw new ReglaNegocioException("Nombre, documento y correo son obligatorios");
        }

        if (form.getIdUsuario() == null) {
            if (usuarioDao.existsByDocumento(documento)) {
                throw new UsuarioDuplicadoException("Ya existe un usuario con ese documento");
            }
            if (usuarioDao.existsByEmailIgnoreCase(email)) {
                throw new UsuarioDuplicadoException("Ya existe un usuario con ese correo");
            }
        } else {
            if (usuarioDao.existsByDocumentoAndIdUsuarioNot(documento, form.getIdUsuario())) {
                throw new UsuarioDuplicadoException("Ya existe otro usuario con ese documento");
            }
            if (usuarioDao.existsByEmailIgnoreCaseAndIdUsuarioNot(email, form.getIdUsuario())) {
                throw new UsuarioDuplicadoException("Ya existe otro usuario con ese correo");
            }
        }

        RolEntity rol = rolDao.findById(form.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol seleccionado no existe"));

        UsuarioEntity usuario;
        if (form.getIdUsuario() == null) {
            usuario = new UsuarioEntity();
            usuario.setEstadoUsuario(Boolean.TRUE);
            if (form.getClave() == null || form.getClave().isBlank()) {
                throw new ReglaNegocioException("La clave es obligatoria para crear un usuario");
            }
            usuario.setClave(passwordEncoder.encode(form.getClave()));
        } else {
            usuario = obtenerPorId(form.getIdUsuario());
            if (form.getClave() != null && !form.getClave().isBlank()) {
                usuario.setClave(passwordEncoder.encode(form.getClave()));
            }
        }

        usuario.setNombre(nombre);
        usuario.setDocumento(documento);
        usuario.setEmail(email);
        usuario.setRol(rol);

        return usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        UsuarioEntity usuario = obtenerPorId(id);
        usuario.setEstadoUsuario(!Boolean.TRUE.equals(usuario.getEstadoUsuario()));
        usuarioDao.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return usuarioDao.countByEstadoUsuarioTrue();
    }
}
