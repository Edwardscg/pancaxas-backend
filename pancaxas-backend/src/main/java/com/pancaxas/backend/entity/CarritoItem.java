package com.pancaxas.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "carrito_items", uniqueConstraints = {
        @UniqueConstraint(name = "uq_carrito_producto", columnNames = {"carrito_id", "producto_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_carrito_items_carrito"))
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_carrito_items_producto"))
    private Producto producto;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @CreationTimestamp
    @Column(name = "agregado_en", nullable = false, updatable = false)
    private LocalDateTime agregadoEn;
}
