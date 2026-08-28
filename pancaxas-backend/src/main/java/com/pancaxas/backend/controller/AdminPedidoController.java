package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.pedido.CambiarEstadoRequest;
import com.pancaxas.backend.dto.pedido.PedidoAdminResumenResponse;
import com.pancaxas.backend.dto.pedido.PedidoResponse;
import com.pancaxas.backend.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final PedidoService pedidoService;

    /** Sin ?estado devuelve todos; con ?estado=EN_PREPARACION filtra (útil para las columnas del Kanban). */
    @GetMapping
    public ResponseEntity<List<PedidoAdminResumenResponse>> listar(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(pedidoService.listarTodos(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerDetalleAdmin(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, request));
    }
}
