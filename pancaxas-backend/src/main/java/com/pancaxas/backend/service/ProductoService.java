package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.catalogo.ProductoRequest;
import com.pancaxas.backend.dto.catalogo.ProductoResponse;
import com.pancaxas.backend.entity.Categoria;
import com.pancaxas.backend.entity.Producto;
import com.pancaxas.backend.exception.RecursoNoEncontradoException;
import com.pancaxas.backend.repository.CategoriaRepository;
import com.pancaxas.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoResponse> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> buscar(String query) {
        return productoRepository.findByDisponibleTrueAndNombreContainingIgnoreCase(query).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> listarDisponibles() {
        return productoRepository.findByDisponibleTrue().stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerDetalle(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = obtenerCategoriaOFallar(request.getCategoriaId());

        Producto producto = Producto.builder()
                .categoria(categoria)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .imagenUrl(request.getImagenUrl())
                .stock(request.getStock())
                .disponible(true)
                .build();

        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerOFallar(id);
        Categoria categoria = obtenerCategoriaOFallar(request.getCategoriaId());

        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setStock(request.getStock());

        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse cambiarDisponibilidad(Long id, boolean disponible) {
        Producto producto = obtenerOFallar(id);
        producto.setDisponible(disponible);
        return aResponse(productoRepository.save(producto));
    }

    private Producto obtenerOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    private Categoria obtenerCategoriaOFallar(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + categoriaId));
    }

    private ProductoResponse aResponse(Producto p) {
        return ProductoResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .imagenUrl(p.getImagenUrl())
                .stock(p.getStock())
                .disponible(p.getDisponible())
                .categoriaId(p.getCategoria().getId())
                .categoriaNombre(p.getCategoria().getNombre())
                .build();
    }
}
