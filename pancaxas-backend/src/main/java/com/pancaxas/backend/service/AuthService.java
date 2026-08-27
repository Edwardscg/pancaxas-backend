package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.auth.AuthResponse;
import com.pancaxas.backend.dto.auth.LoginRequest;
import com.pancaxas.backend.dto.auth.RegisterRequest;
import com.pancaxas.backend.entity.Rol;
import com.pancaxas.backend.entity.Usuario;
import com.pancaxas.backend.exception.CredencialesInvalidasException;
import com.pancaxas.backend.exception.EmailYaRegistradoException;
import com.pancaxas.backend.repository.UsuarioRepository;
import com.pancaxas.backend.security.JwtService;
import com.pancaxas.backend.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new EmailYaRegistradoException(request.getCorreo());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .estado(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String token = jwtService.generarToken(principal);

        return construirRespuesta(usuario, token);
    }

    public AuthResponse iniciarSesion(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(CredencialesInvalidasException::new);

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String token = jwtService.generarToken(principal);

        return construirRespuesta(usuario, token);
    }

    private AuthResponse construirRespuesta(Usuario usuario, String token) {
        return AuthResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }
}
