package com.pancaxas.backend.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class HistorialEstadoResponse {
    private String estado;
    private String comentario;
    private LocalDateTime fechaCambio;
}
