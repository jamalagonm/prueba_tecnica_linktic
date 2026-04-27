package com.microservicio.producto.service;

import com.microservicio.producto.dto.ProductoRequest;
import com.microservicio.producto.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse obtenerProductoPorId(Long id);

    ProductoResponse crearProducto(ProductoRequest request);

    List<ProductoResponse> listarTodosLosProductos();

    String obtenerNombreProducto(Long productoId);
}
