package com.pancaxas.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_pedido", uniqueConstraints = {
        @UniqueConstraint(name = "uq_estados_pedido_nombre", columnNames = "nombre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false)
    private Integer orden;
}
