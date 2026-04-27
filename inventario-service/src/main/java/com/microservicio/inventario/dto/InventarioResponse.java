package com.microservicio.inventario.dto;

import com.microservicio.inventario.entity.Inventario;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {

    private Long id;
    private Long productoId;
    private Integer cantidad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static InventarioResponse fromEntity(Inventario inventario) {
        return InventarioResponse.builder()
                .id(inventario.getId())
                .productoId(inventario.getProductoId())
                .cantidad(inventario.getCantidad())
                .fechaCreacion(inventario.getFechaCreacion())
                .fechaModificacion(inventario.getFechaModificacion())
                .build();
    }

}
