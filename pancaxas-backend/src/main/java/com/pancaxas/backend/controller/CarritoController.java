package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.carrito.ActualizarCantidadRequest;
import com.pancaxas.backend.dto.carrito.AgregarItemRequest;
import com.pancaxas.backend.dto.carrito.CarritoResponse;
import com.pancaxas.backend.security.UsuarioPrincipal;
import com.pancaxas.backend.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(principal.getUsuario().getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarProducto(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                             @Valid @RequestBody AgregarItemRequest request) {
        return ResponseEntity.ok(carritoService.agregarProducto(principal.getUsuario().getId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                                @PathVariable Long itemId,
                                                                @Valid @RequestBody ActualizarCantidadRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(
                principal.getUsuario().getId(), itemId, request.getCantidad()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> eliminarItem(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                          @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(principal.getUsuario().getId(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@AuthenticationPrincipal UsuarioPrincipal principal) {
        carritoService.vaciarCarrito(principal.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }
}
