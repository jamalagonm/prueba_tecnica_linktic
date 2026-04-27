package com.microservicio.inventario.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CompraRequest {
    private Long productoId;
    private Integer cantidad;
}
