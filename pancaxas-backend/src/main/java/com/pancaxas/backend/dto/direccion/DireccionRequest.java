package com.pancaxas.backend.dto.direccion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DireccionRequest {

    private String alias;

    @NotBlank(message = "El distrito es obligatorio")
    private String distrito;

    @NotBlank(message = "La dirección completa es obligatoria")
    private String direccionCompleta;

    private String referencia;

    private BigDecimal latitud;

    private BigDecimal longitud;

    private Boolean esPredeterminada;
}
