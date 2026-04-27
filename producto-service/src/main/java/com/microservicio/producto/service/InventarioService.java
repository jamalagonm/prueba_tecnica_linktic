package com.microservicio.producto.service;

import com.microservicio.producto.entity.Producto;

public interface InventarioService {

    void crearProductoInventario(Producto producto, Integer cantidad);
}
