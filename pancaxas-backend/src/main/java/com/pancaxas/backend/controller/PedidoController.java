package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.pedido.CheckoutRequest;
import com.pancaxas.backend.dto.pedido.PedidoResponse;
import com.pancaxas.backend.dto.pedido.PedidoResumenResponse;
import com.pancaxas.backend.security.UsuarioPrincipal;
import com.pancaxas.backend.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponse> checkout(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                     @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.checkout(principal.getUsuario().getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResumenResponse>> listarMisPedidos(
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.ok(pedidoService.listarDelUsuario(principal.getUsuario().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerDetalle(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                           @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerDetalle(principal.getUsuario().getId(), id));
    }
}
