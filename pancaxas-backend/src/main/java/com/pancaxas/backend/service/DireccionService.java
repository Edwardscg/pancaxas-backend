package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.direccion.DireccionRequest;
import com.pancaxas.backend.dto.direccion.DireccionResponse;
import com.pancaxas.backend.entity.Direccion;
import com.pancaxas.backend.entity.Usuario;
import com.pancaxas.backend.exception.AccesoDenegadoException;
import com.pancaxas.backend.exception.RecursoNoEncontradoException;
import com.pancaxas.backend.repository.DireccionRepository;
import com.pancaxas.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public List<DireccionResponse> listarDelUsuario(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DireccionResponse crear(Long usuarioId, DireccionRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + usuarioId));

        Direccion direccion = Direccion.builder()
                .usuario(usuario)
                .alias(request.getAlias())
                .distrito(request.getDistrito())
                .direccionCompleta(request.getDireccionCompleta())
                .referencia(request.getReferencia())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .esPredeterminada(Boolean.TRUE.equals(request.getEsPredeterminada()))
                .build();

        return aResponse(direccionRepository.save(direccion));
    }

    @Transactional
    public void eliminar(Long usuarioId, Long direccionId) {
        Direccion direccion = obtenerDelUsuario(usuarioId, direccionId);
        direccionRepository.delete(direccion);
    }

    /** Usado también por PedidoService para validar la dirección del checkout. */
    Direccion obtenerDelUsuario(Long usuarioId, Long direccionId) {
        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Dirección no encontrada con id: " + direccionId));

        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new AccesoDenegadoException("Esta dirección no pertenece al usuario autenticado");
        }
        return direccion;
    }

    /** Público para que PedidoService reutilice el mismo mapeo en la respuesta del pedido. */
    public DireccionResponse aResponse(Direccion d) {
        return DireccionResponse.builder()
                .id(d.getId())
                .alias(d.getAlias())
                .distrito(d.getDistrito())
                .direccionCompleta(d.getDireccionCompleta())
                .referencia(d.getReferencia())
                .esPredeterminada(d.getEsPredeterminada())
                .build();
    }
}
