package com.microservicio.inventario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_compras")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class HistorialCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String descripcion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fecha;

}
