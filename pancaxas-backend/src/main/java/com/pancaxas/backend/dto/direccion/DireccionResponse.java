package com.pancaxas.backend.dto.direccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DireccionResponse {
    private Long id;
    private String alias;
    private String distrito;
    private String direccionCompleta;
    private String referencia;
    private Boolean esPredeterminada;
}
