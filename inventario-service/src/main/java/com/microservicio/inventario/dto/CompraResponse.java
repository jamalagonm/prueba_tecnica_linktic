package com.microservicio.inventario.dto;

import com.microservicio.inventario.entity.HistorialCompra;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CompraResponse {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private String nombreProducto;
    private LocalDateTime fecha;

    public static CompraResponse fromEntity(HistorialCompra historialCompra, String nombreProducto){
        return CompraResponse.builder()
                .id(historialCompra.getId())
                .productoId(historialCompra.getProductoId())
                .cantidad(historialCompra.getCantidad())
                .nombreProducto(nombreProducto)
                .fecha(historialCompra.getFecha())
                .build();
    }
}
