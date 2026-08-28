package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.catalogo.ProductoRequest;
import com.pancaxas.backend.dto.catalogo.ProductoResponse;
import com.pancaxas.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // -------- Público (clientes) --------
    @GetMapping("/api/productos")
    public ResponseEntity<List<ProductoResponse>> listarPorCategoria(
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping("/api/productos/{id}")
    public ResponseEntity<ProductoResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerDetalle(id));
    }

    @GetMapping("/api/productos/buscar")
    public ResponseEntity<List<ProductoResponse>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(productoService.buscar(q));
    }

    // -------- Administrador --------
    @GetMapping("/api/admin/productos")
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping("/api/admin/productos")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/api/admin/productos/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @PatchMapping("/api/admin/productos/{id}/disponibilidad")
    public ResponseEntity<ProductoResponse> cambiarDisponibilidad(@PathVariable Long id,
                                                                    @RequestParam boolean disponible) {
        return ResponseEntity.ok(productoService.cambiarDisponibilidad(id, disponible));
    }
}
