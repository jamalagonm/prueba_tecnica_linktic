package com.microservicio.inventario.serviceImpl;

import com.microservicio.inventario.dto.InventarioRequest;
import com.microservicio.inventario.dto.InventarioResponse;
import com.microservicio.inventario.entity.Inventario;
import com.microservicio.inventario.exception.ProductoAlreadyExistsException;
import com.microservicio.inventario.exception.ProductoNotFoundException;
import com.microservicio.inventario.repository.InventarioRepository;
import com.microservicio.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    @Override
    public InventarioResponse obtenerProducto(Long productoId) {
        log.info("[obtenerProducto] Consultando inventario - productoId: {}", productoId);

        Inventario inventario = inventarioRepository.obtenerInventario(productoId)
                .orElseThrow(() -> {
                    log.warn("[obtenerProducto] Producto no encontrado en inventario - productoId: {}", productoId);
                    return new ProductoNotFoundException(
                            "Producto no encontrado con ID: " + productoId);
                });

        log.debug("[obtenerProducto] Inventario encontrado - productoId: {}, stockDisponible: {}",
                productoId, inventario.getCantidad());

        return InventarioResponse.fromEntity(inventario);
    }

    @Override
    public InventarioResponse crearInventarioProducto(InventarioRequest inventarioRequest) {
        log.info("[crearInventario] Inicio - productoId: {}, cantidad: {}",
                inventarioRequest.getProductoId(), inventarioRequest.getCantidad());

        Optional<Inventario> inventario = inventarioRepository.obtenerInventario(inventarioRequest.getProductoId());

        if (inventario.isPresent()) {
            log.warn("[crearInventario] Producto ya existe en inventario - productoId: {}, stockActual: {}",
                    inventarioRequest.getProductoId(), inventario.get().getCantidad());
            throw new ProductoAlreadyExistsException("Producto ya existente en inventario");
        }

        Inventario inventarioGuardado = inventarioRepository.save(inventarioRequest.toEntity());

        log.info("[crearInventario] Inventario creado exitosamente - productoId: {}, cantidad: {}, inventarioId: {}",
                inventarioGuardado.getProductoId(), inventarioGuardado.getCantidad(), inventarioGuardado.getId());

        return InventarioResponse.fromEntity(inventarioGuardado);
    }

    @Override
    @Transactional
    public InventarioResponse actualizarInventarioProducto(InventarioRequest inventarioRequest) {
        log.info("[actualizarInventario] Inicio - productoId: {}, nuevaCantidad: {}",
                inventarioRequest.getProductoId(), inventarioRequest.getCantidad());

        Inventario inventario = inventarioRepository.obtenerInventario(inventarioRequest.getProductoId())
                .orElseThrow(() -> {
                    log.warn("[actualizarInventario] Producto no encontrado - productoId: {}",
                            inventarioRequest.getProductoId());
                    return new ProductoNotFoundException(
                            "Producto no encontrado con ID: " + inventarioRequest.getProductoId());
                });

        log.debug("[actualizarInventario] Stock actual: {}, nuevoStock: {}, productoId: {}",
                inventario.getCantidad(), inventarioRequest.getCantidad(), inventarioRequest.getProductoId());

        LocalDateTime fechaModificacion = LocalDateTime.now();
        inventarioRepository.actualizarInventario(inventarioRequest.getProductoId(),
                inventarioRequest.getCantidad(), fechaModificacion);
        inventario.setCantidad(inventarioRequest.getCantidad());
        inventario.setFechaModificacion(fechaModificacion);

        log.info("[actualizarInventario] Inventario actualizado - productoId: {}, cantidad: {}",
                inventarioRequest.getProductoId(), inventarioRequest.getCantidad());

        return InventarioResponse.fromEntity(inventario);
    }

    @Override
    @Transactional
    public InventarioResponse decrementarInventarioProducto(InventarioRequest inventarioRequest) {
        log.info("[decrementarInventario] Inicio - productoId: {}, cantidadResultante: {}",
                inventarioRequest.getProductoId(), inventarioRequest.getCantidad());

        Inventario inventario = inventarioRepository.obtenerInventario(inventarioRequest.getProductoId())
                .orElseThrow(() -> {
                    log.warn("[decrementarInventario] Producto no encontrado - productoId: {}",
                            inventarioRequest.getProductoId());
                    return new ProductoNotFoundException(
                            "Producto no encontrado con ID: " + inventarioRequest.getProductoId());
                });

        int stockAnterior = inventario.getCantidad();
        int unidadesDescontadas = stockAnterior - inventarioRequest.getCantidad();
        log.debug("[decrementarInventario] stockAnterior: {}, unidadesDescontadas: {}, stockResultante: {}, productoId: {}",
                stockAnterior, unidadesDescontadas, inventarioRequest.getCantidad(), inventarioRequest.getProductoId());

        LocalDateTime fechaModificacion = LocalDateTime.now();
        inventarioRepository.actualizarInventario(inventarioRequest.getProductoId(),
                inventarioRequest.getCantidad(), fechaModificacion);
        inventario.setCantidad(inventarioRequest.getCantidad());
        inventario.setFechaModificacion(fechaModificacion);

        log.info("[decrementarInventario] Stock decrementado - productoId: {}, stockAnterior: {}, stockNuevo: {}, unidadesDescontadas: {}",
                inventarioRequest.getProductoId(), stockAnterior, inventarioRequest.getCantidad(), unidadesDescontadas);

        return InventarioResponse.fromEntity(inventario);
    }


}
