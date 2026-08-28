package com.pancaxas.backend.dto.pedido;

import com.pancaxas.backend.dto.direccion.DireccionResponse;
import com.pancaxas.backend.entity.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private String codigoPedido;
    private String estado;
    private MetodoPago metodoPago;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;
    private String notas;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaEntregaEstimada;
    private DireccionResponse direccion;
    private List<PedidoDetalleResponse> detalles;
    private List<HistorialEstadoResponse> historial;
}
