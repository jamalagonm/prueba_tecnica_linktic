package com.microservicio.inventario.dto;

import com.microservicio.inventario.entity.Inventario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El productoId no puede ser negativo")
    private Long productoId;
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer cantidad;

    public Inventario toEntity() {
        return Inventario.builder()
                .productoId(this.productoId)
                .cantidad(this.cantidad)
                .build();
    }

}
