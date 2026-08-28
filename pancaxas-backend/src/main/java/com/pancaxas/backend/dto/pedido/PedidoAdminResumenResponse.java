package com.pancaxas.backend.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PedidoAdminResumenResponse {
    private Long id;
    private String codigoPedido;
    private String estado;
    private String clienteNombre;
    private String clienteCorreo;
    private BigDecimal total;
    private LocalDateTime fechaPedido;
}
