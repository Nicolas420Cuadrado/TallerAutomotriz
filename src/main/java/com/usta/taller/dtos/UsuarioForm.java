package com.usta.taller.dtos;

import com.usta.taller.entities.UsuarioEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioForm {

    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    private String documento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String email;

    @Size(max = 100, message = "La clave no puede superar 100 caracteres")
    private String clave;

    @NotNull(message = "El rol es obligatorio")
    private Long idRol;

    public static UsuarioForm desde(UsuarioEntity usuario) {
        UsuarioForm form = new UsuarioForm();
        form.setIdUsuario(usuario.getIdUsuario());
        form.setNombre(usuario.getNombre());
        form.setDocumento(usuario.getDocumento());
        form.setEmail(usuario.getEmail());
        if (usuario.getRol() != null) {
            form.setIdRol(usuario.getRol().getIdRol());
        }
        return form;
    }
}
