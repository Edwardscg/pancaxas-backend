package com.pancaxas.backend.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer stock;
    private Boolean disponible;
    private Long categoriaId;
    private String categoriaNombre;
}
