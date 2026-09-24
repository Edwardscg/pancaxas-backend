package com.pancaxas.backend.controller;

import com.pancaxas.backend.dto.auth.AuthResponse;
import com.pancaxas.backend.dto.auth.LoginRequest;
import com.pancaxas.backend.dto.auth.RegisterRequest;
import com.pancaxas.backend.dto.auth.UsuarioActualResponse;
import com.pancaxas.backend.security.UsuarioPrincipal;
import com.pancaxas.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        AuthResponse respuesta = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse respuesta = authService.iniciarSesion(request);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioActualResponse> me(@AuthenticationPrincipal UsuarioPrincipal principal) {
        var usuario = principal.getUsuario();
        return ResponseEntity.ok(UsuarioActualResponse.builder()
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build());
    }
}
