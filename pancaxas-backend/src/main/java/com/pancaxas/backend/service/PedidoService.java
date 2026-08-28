package com.pancaxas.backend.service;

import com.pancaxas.backend.dto.pedido.CheckoutRequest;
import com.pancaxas.backend.dto.pedido.CambiarEstadoRequest;
import com.pancaxas.backend.dto.pedido.HistorialEstadoResponse;
import com.pancaxas.backend.dto.pedido.PedidoAdminResumenResponse;
import com.pancaxas.backend.dto.pedido.PedidoDetalleResponse;
import com.pancaxas.backend.dto.pedido.PedidoResponse;
import com.pancaxas.backend.dto.pedido.PedidoResumenResponse;
import com.pancaxas.backend.entity.Carrito;
import com.pancaxas.backend.entity.CarritoItem;
import com.pancaxas.backend.entity.Direccion;
import com.pancaxas.backend.entity.EstadoPedido;
import com.pancaxas.backend.entity.HistorialEstadoPedido;
import com.pancaxas.backend.entity.Pedido;
import com.pancaxas.backend.entity.PedidoDetalle;
import com.pancaxas.backend.entity.Producto;
import com.pancaxas.backend.exception.AccesoDenegadoException;
import com.pancaxas.backend.exception.CarritoVacioException;
import com.pancaxas.backend.exception.RecursoNoEncontradoException;
import com.pancaxas.backend.exception.StockInsuficienteException;
import com.pancaxas.backend.repository.CarritoRepository;
import com.pancaxas.backend.repository.EstadoPedidoRepository;
import com.pancaxas.backend.repository.PedidoRepository;
import com.pancaxas.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final BigDecimal COSTO_ENVIO = new BigDecimal("5.00");
    private static final String ESTADO_RECIBIDO = "RECIBIDO";

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final DireccionService direccionService;

    @Transactional
    public PedidoResponse checkout(Long usuarioId, CheckoutRequest request) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(CarritoVacioException::new);

        if (carrito.getItems().isEmpty()) {
            throw new CarritoVacioException();
        }

        Direccion direccion = direccionService.obtenerDelUsuario(usuarioId, request.getDireccionId());

        EstadoPedido estadoRecibido = estadoPedidoRepository.findByNombre(ESTADO_RECIBIDO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El estado 'RECIBIDO' no está configurado en estados_pedido"));

        BigDecimal subtotal = BigDecimal.ZERO;
        List<PedidoDetalle> detalles = new ArrayList<>();

        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();

            if (!Boolean.TRUE.equals(producto.getDisponible()) || producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para '" + producto.getNombre() + "' al confirmar el pedido");
            }

            BigDecimal subtotalItem = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotal = subtotal.add(subtotalItem);

            detalles.add(PedidoDetalle.builder()
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(subtotalItem)
                    .build());

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        BigDecimal total = subtotal.add(COSTO_ENVIO);

        Pedido pedido = Pedido.builder()
                .codigoPedido("TEMP-" + System.nanoTime())
                .usuario(carrito.getUsuario())
                .direccion(direccion)
                .estado(estadoRecibido)
                .metodoPago(request.getMetodoPago())
                .subtotal(subtotal)
                .costoEnvio(COSTO_ENVIO)
                .total(total)
                .notas(request.getNotas())
                .fechaEntregaEstimada(LocalDateTime.now().plusMinutes(45))
                .build();

        for (PedidoDetalle detalle : detalles) {
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
        }

        pedido = pedidoRepository.save(pedido);
        pedido.setCodigoPedido(String.format("PC-%05d", pedido.getId()));

        pedido.getHistorial().add(HistorialEstadoPedido.builder()
                .pedido(pedido)
                .estado(estadoRecibido)
                .comentario("Pedido registrado")
                .build());

        pedido = pedidoRepository.save(pedido);

        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return aResponse(pedido);
    }

    public PedidoResponse obtenerDetalle(Long usuarioId, Long pedidoId) {
        return aResponse(obtenerYVerificarPropietario(usuarioId, pedidoId));
    }

    public List<PedidoResumenResponse> listarDelUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId).stream()
                .map(this::aResumen)
                .collect(Collectors.toList());
    }

    // -------- Administrador (panel de pedidos) --------

    public List<PedidoAdminResumenResponse> listarTodos(String estado) {
        List<Pedido> pedidos = (estado != null && !estado.isBlank())
                ? pedidoRepository.findByEstado_NombreOrderByFechaPedidoDesc(estado.toUpperCase())
                : pedidoRepository.findAllByOrderByFechaPedidoDesc();

        return pedidos.stream().map(this::aResumenAdmin).collect(Collectors.toList());
    }

    public PedidoResponse obtenerDetalleAdmin(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));
        return aResponse(pedido);
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long pedidoId, CambiarEstadoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(request.getEstado().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estado no válido: " + request.getEstado()
                                + ". Usa RECIBIDO, EN_PREPARACION, EN_CAMINO, ENTREGADO o CANCELADO"));

        pedido.setEstado(nuevoEstado);
        pedido.getHistorial().add(HistorialEstadoPedido.builder()
                .pedido(pedido)
                .estado(nuevoEstado)
                .comentario(request.getComentario())
                .build());

        pedido = pedidoRepository.save(pedido);
        return aResponse(pedido);
    }

    private PedidoAdminResumenResponse aResumenAdmin(Pedido p) {
        return PedidoAdminResumenResponse.builder()
                .id(p.getId())
                .codigoPedido(p.getCodigoPedido())
                .estado(p.getEstado().getNombre())
                .clienteNombre(p.getUsuario().getNombre() + " " + p.getUsuario().getApellido())
                .clienteCorreo(p.getUsuario().getCorreo())
                .total(p.getTotal())
                .fechaPedido(p.getFechaPedido())
                .build();
    }

    private Pedido obtenerYVerificarPropietario(Long usuarioId, Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw new AccesoDenegadoException("Este pedido no pertenece al usuario autenticado");
        }
        return pedido;
    }

    private PedidoResumenResponse aResumen(Pedido p) {
        return PedidoResumenResponse.builder()
                .id(p.getId())
                .codigoPedido(p.getCodigoPedido())
                .estado(p.getEstado().getNombre())
                .total(p.getTotal())
                .fechaPedido(p.getFechaPedido())
                .build();
    }

    private PedidoResponse aResponse(Pedido p) {
        List<PedidoDetalleResponse> detalles = p.getDetalles().stream()
                .map(d -> PedidoDetalleResponse.builder()
                        .productoId(d.getProducto().getId())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        List<HistorialEstadoResponse> historial = p.getHistorial().stream()
                .sorted(Comparator.comparing(HistorialEstadoPedido::getFechaCambio))
                .map(h -> HistorialEstadoResponse.builder()
                        .estado(h.getEstado().getNombre())
                        .comentario(h.getComentario())
                        .fechaCambio(h.getFechaCambio())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .id(p.getId())
                .codigoPedido(p.getCodigoPedido())
                .estado(p.getEstado().getNombre())
                .metodoPago(p.getMetodoPago())
                .subtotal(p.getSubtotal())
                .costoEnvio(p.getCostoEnvio())
                .total(p.getTotal())
                .notas(p.getNotas())
                .fechaPedido(p.getFechaPedido())
                .fechaEntregaEstimada(p.getFechaEntregaEstimada())
                .direccion(direccionService.aResponse(p.getDireccion()))
                .detalles(detalles)
                .historial(historial)
                .build();
    }
}
