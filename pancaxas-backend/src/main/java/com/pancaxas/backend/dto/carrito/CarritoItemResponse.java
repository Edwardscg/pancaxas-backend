package com.pancaxas.backend.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class CarritoItemResponse {
    private Long itemId;
    private Long productoId;
    private String productoNombre;
    private String productoImagenUrl;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
    private Boolean disponible;
}
