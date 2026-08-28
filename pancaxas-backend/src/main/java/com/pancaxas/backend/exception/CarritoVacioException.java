package com.pancaxas.backend.exception;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException() {
        super("El carrito está vacío, agrega productos antes de confirmar el pedido");
    }
}
