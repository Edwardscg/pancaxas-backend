package com.pancaxas.backend.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoRequest {

    /** RECIBIDO, EN_PREPARACION, EN_CAMINO, ENTREGADO o CANCELADO (ver tabla estados_pedido). */
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private String comentario;
}
