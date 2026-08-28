package com.pancaxas.backend.repository;

import com.pancaxas.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    List<Producto> findByDisponibleTrueAndNombreContainingIgnoreCase(String nombre);

    List<Producto> findByDisponibleTrue();
}
