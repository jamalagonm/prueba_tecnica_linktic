package com.microservicio.producto.serviceImpl;

import com.microservicio.producto.dto.ProductoRequest;
import com.microservicio.producto.dto.ProductoResponse;
import com.microservicio.producto.entity.Producto;
import com.microservicio.producto.exception.ProductoNotFoundException;
import com.microservicio.producto.repository.ProductoRepository;
import com.microservicio.producto.service.InventarioService;
import com.microservicio.producto.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public ProductoResponse crearProducto(ProductoRequest request) {
        log.info("[crearProducto] Inicio - nombre: {}, precio: {}, cantidad: {}",
                request.getNombre(), request.getPrecio(), request.getCantidad());

        Producto guardado = guardarProducto(request);
        log.info("[crearProducto] Producto guardado en BD - ID: {}", guardado.getId());
        try {
            log.info("[crearProducto] Creando inventario para producto ID: {}", guardado.getId());
            inventarioService.crearProductoInventario(guardado, request.getCantidad());
            log.info("[crearProducto] Inventario creado exitosamente para producto ID: {}", guardado.getId());
        } catch (Exception e) {
            log.error("[crearProducto] Error al crear inventario para producto ID: {} - Error: {}",
                    guardado.getId(), e.getMessage());
            productoRepository.deleteById(guardado.getId());
            log.warn("[crearProducto] Rollback ejecutado - Producto ID: {} eliminado", guardado.getId());
            throw new RuntimeException("No se pudo crear el inventario, operación revertida");
        }
        log.info("[crearProducto] Proceso completado exitosamente - Producto ID: {}", guardado.getId());
        return ProductoResponse.fromEntity(guardado);
    }

    @Transactional
    public Producto guardarProducto(ProductoRequest request) {
        return productoRepository.save(Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .build());
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoPorId(Long id) {
        log.info("[obtenerProductoPorId] Consultando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[obtenerProductoPorId] Producto no encontrado con ID: {}", id);
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        log.debug("[obtenerProductoPorId] Producto encontrado - nombre: {}", producto.getNombre());
        return ProductoResponse.fromEntity(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodosLosProductos() {
        log.info("[listarTodosLosProductos] Consultando todos los productos");

        List<ProductoResponse> productos = productoRepository.findAll()
                .stream()
                .map(ProductoResponse::fromEntity)
                .toList();

        log.info("[listarTodosLosProductos] Total de productos encontrados: {}", productos.size());
        return productos;
    }

    public String obtenerNombreProducto(Long productoId) {
        log.info("[obtenerNombreProducto] Consultando nombre del producto ID: {}", productoId);

        String nombre = productoRepository.obtenerNombre(productoId);

        if (nombre == null) {
            log.warn("[obtenerNombreProducto] No se encontró nombre para producto ID: {}", productoId);
        } else {
            log.debug("[obtenerNombreProducto] Nombre obtenido: {} para producto ID: {}", nombre, productoId);
        }

        return nombre;
    }
}
