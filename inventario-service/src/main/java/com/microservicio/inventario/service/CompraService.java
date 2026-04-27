package com.microservicio.inventario.service;

import com.microservicio.inventario.dto.CompraResponse;
import com.microservicio.inventario.dto.CompraRequest;

public interface CompraService {

    CompraResponse generarCompra (CompraRequest compraRequest);

}
