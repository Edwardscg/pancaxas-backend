package com.pancaxas.backend.dto.auth;

import com.pancaxas.backend.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UsuarioActualResponse {
    private Long usuarioId;
    private String nombre;
    private String correo;
    private Rol rol;
}
