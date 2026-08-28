package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.catalogo.CategoriaRequest;
import com.pancaxas.backend.dto.catalogo.CategoriaResponse;
import com.pancaxas.backend.entity.Categoria;
import com.pancaxas.backend.exception.RecursoNoEncontradoException;
import com.pancaxas.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listarActivas() {
        return categoriaRepository.findByEstadoTrueOrderByOrdenAsc().stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAllByOrderByOrdenAsc().stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .imagenUrl(request.getImagenUrl())
                .orden(request.getOrden())
                .estado(true)
                .build();
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obtenerOFallar(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setImagenUrl(request.getImagenUrl());
        categoria.setOrden(request.getOrden());
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse cambiarEstado(Long id, boolean estado) {
        Categoria categoria = obtenerOFallar(id);
        categoria.setEstado(estado);
        return aResponse(categoriaRepository.save(categoria));
    }

    private Categoria obtenerOFallar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    private CategoriaResponse aResponse(Categoria c) {
        return CategoriaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .imagenUrl(c.getImagenUrl())
                .orden(c.getOrden())
                .estado(c.getEstado())
                .build();
    }
}
