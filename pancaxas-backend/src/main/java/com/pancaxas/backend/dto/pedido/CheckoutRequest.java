package com.pancaxas.backend.dto.pedido;

import com.pancaxas.backend.entity.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotNull(message = "Debe seleccionar una dirección de entrega")
    private Long direccionId;

    @NotNull(message = "Debe seleccionar un método de pago")
    private MetodoPago metodoPago;

    private String notas;
}
