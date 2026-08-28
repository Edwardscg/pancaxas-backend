package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.carrito.AgregarItemRequest;
import com.pancaxas.backend.dto.carrito.CarritoItemResponse;
import com.pancaxas.backend.dto.carrito.CarritoResponse;
import com.pancaxas.backend.entity.Carrito;
import com.pancaxas.backend.entity.CarritoItem;
import com.pancaxas.backend.entity.Producto;
import com.pancaxas.backend.entity.Usuario;
import com.pancaxas.backend.exception.AccesoDenegadoException;
import com.pancaxas.backend.exception.RecursoNoEncontradoException;
import com.pancaxas.backend.exception.StockInsuficienteException;
import com.pancaxas.backend.repository.CarritoItemRepository;
import com.pancaxas.backend.repository.CarritoRepository;
import com.pancaxas.backend.repository.ProductoRepository;
import com.pancaxas.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoResponse obtenerCarrito(Long usuarioId) {
        return aResponse(obtenerOCrearCarrito(usuarioId));
    }

    @Transactional
    public CarritoResponse agregarProducto(Long usuarioId, AgregarItemRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + request.getProductoId()));

        if (!Boolean.TRUE.equals(producto.getDisponible())) {
            throw new StockInsuficienteException(
                    "El producto '" + producto.getNombre() + "' no está disponible");
        }

        CarritoItem item = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);

        int cantidadFinal = request.getCantidad() + (item != null ? item.getCantidad() : 0);

        if (producto.getStock() < cantidadFinal) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
        }

        if (item != null) {
            item.setCantidad(cantidadFinal);
        } else {
            item = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(request.getCantidad())
                    .build();
            carrito.getItems().add(item);
        }

        carritoItemRepository.save(item);

        return aResponse(carrito);
    }

    @Transactional
    public CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        CarritoItem item = obtenerItemDelUsuario(usuarioId, itemId);

        if (item.getProducto().getStock() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + item.getProducto().getNombre() +
                            "'. Disponible: " + item.getProducto().getStock());
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);

        return aResponse(item.getCarrito());
    }

    @Transactional
    public CarritoResponse eliminarItem(Long usuarioId, Long itemId) {
        CarritoItem item = obtenerItemDelUsuario(usuarioId, itemId);
        Carrito carrito = item.getCarrito();
        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);
        return aResponse(carrito);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private CarritoItem obtenerItemDelUsuario(Long usuarioId, Long itemId) {
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ítem de carrito no encontrado con id: " + itemId));

        if (!item.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new AccesoDenegadoException("Este ítem no pertenece al carrito del usuario autenticado");
        }
        return item;
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RecursoNoEncontradoException(
                                    "Usuario no encontrado con id: " + usuarioId));
                    Carrito nuevo = Carrito.builder()
                            .usuario(usuario)
                            .build();
                    return carritoRepository.save(nuevo);
                });
    }

    private CarritoResponse aResponse(Carrito carrito) {
        List<CarritoItemResponse> items = carrito.getItems().stream()
                .map(this::aItemResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(CarritoItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream().mapToInt(CarritoItemResponse::getCantidad).sum();

        return CarritoResponse.builder()
                .carritoId(carrito.getId())
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
    }

    private CarritoItemResponse aItemResponse(CarritoItem item) {
        Producto producto = item.getProducto();
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));

        return CarritoItemResponse.builder()
                .itemId(item.getId())
                .productoId(producto.getId())
                .productoNombre(producto.getNombre())
                .productoImagenUrl(producto.getImagenUrl())
                .precioUnitario(producto.getPrecio())
                .cantidad(item.getCantidad())
                .subtotal(subtotal)
                .disponible(producto.getDisponible())
                .build();
    }
}
