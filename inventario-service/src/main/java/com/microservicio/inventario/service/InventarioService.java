package com.microservicio.inventario.service;

import com.microservicio.inventario.dto.InventarioRequest;
import com.microservicio.inventario.dto.InventarioResponse;

public interface InventarioService {

    InventarioResponse obtenerProducto(Long productoId);

    InventarioResponse crearInventarioProducto(InventarioRequest inventarioRequest);

    InventarioResponse actualizarInventarioProducto(InventarioRequest inventarioRequest);

    InventarioResponse decrementarInventarioProducto(InventarioRequest inventarioRequest);



}
