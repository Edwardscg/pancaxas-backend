package com.pancaxas.backend.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PedidoResumenResponse {
    private Long id;
    private String codigoPedido;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fechaPedido;
}
