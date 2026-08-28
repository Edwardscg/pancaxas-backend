package com.pancaxas.backend.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoriaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private Integer orden;
    private Boolean estado;
}
