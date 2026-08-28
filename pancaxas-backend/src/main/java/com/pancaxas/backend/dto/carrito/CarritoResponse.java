package com.pancaxas.backend.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CarritoResponse {
    private Long carritoId;
    private List<CarritoItemResponse> items;
    private BigDecimal subtotal;
    private Integer totalItems;
}
