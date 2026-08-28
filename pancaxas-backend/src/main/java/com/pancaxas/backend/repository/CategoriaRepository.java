package com.pancaxas.backend.repository;

import com.pancaxas.backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByEstadoTrueOrderByOrdenAsc();

    List<Categoria> findAllByOrderByOrdenAsc();

    boolean existsByNombreIgnoreCase(String nombre);
}
