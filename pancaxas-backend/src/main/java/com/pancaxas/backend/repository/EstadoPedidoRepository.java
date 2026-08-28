package com.pancaxas.backend.repository;

import com.pancaxas.backend.entity.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {
    Optional<EstadoPedido> findByNombre(String nombre);
}
