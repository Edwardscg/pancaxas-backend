package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.catalogo.CategoriaRequest;
import com.pancaxas.backend.dto.catalogo.CategoriaResponse;
import com.pancaxas.backend.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // -------- Público (clientes): solo categorías activas --------
    @GetMapping("/api/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarActivas() {
        return ResponseEntity.ok(categoriaService.listarActivas());
    }

    // -------- Administrador: todas, incluidas las deshabilitadas --------
    @GetMapping("/api/admin/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @PostMapping("/api/admin/categorias")
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(request));
    }

    @PutMapping("/api/admin/categorias/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @PatchMapping("/api/admin/categorias/{id}/estado")
    public ResponseEntity<CategoriaResponse> cambiarEstado(@PathVariable Long id,
                                                             @RequestParam boolean estado) {
        return ResponseEntity.ok(categoriaService.cambiarEstado(id, estado));
    }
}
