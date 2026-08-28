package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.direccion.DireccionRequest;
import com.pancaxas.backend.dto.direccion.DireccionResponse;
import com.pancaxas.backend.security.UsuarioPrincipal;
import com.pancaxas.backend.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @GetMapping
    public ResponseEntity<List<DireccionResponse>> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.ok(direccionService.listarDelUsuario(principal.getUsuario().getId()));
    }

    @PostMapping
    public ResponseEntity<DireccionResponse> crear(@AuthenticationPrincipal UsuarioPrincipal principal,
                                                     @Valid @RequestBody DireccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(direccionService.crear(principal.getUsuario().getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal,
                                          @PathVariable Long id) {
        direccionService.eliminar(principal.getUsuario().getId(), id);
        return ResponseEntity.noContent().build();
    }
}
