package com.microservicio.inventario.serviceImpl;

import com.microservicio.inventario.dto.*;
import com.microservicio.inventario.entity.HistorialCompra;
import com.microservicio.inventario.exception.ProductoOutOfStock;
import com.microservicio.inventario.repository.CompraRepository;
import com.microservicio.inventario.service.CompraService;
import com.microservicio.inventario.service.InventarioService;
import com.microservicio.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraServiceImpl implements CompraService {

   private final InventarioService inventarioService;
   private final ProductoService productoService;
   private final CompraRepository compraRepository;


    @Override
    @Transactional
    public CompraResponse generarCompra(CompraRequest compraRequest) {
        log.info("[generarCompra] Inicio - productoId: {}, cantidadSolicitada: {}",
                compraRequest.getProductoId(), compraRequest.getCantidad());

        InventarioResponse inventarioResponse = inventarioService.obtenerProducto(compraRequest.getProductoId());
        log.info("[generarCompra] Stock actual: {}, stockSolicitado: {}, productoId: {}",
                inventarioResponse.getCantidad(), compraRequest.getCantidad(), compraRequest.getProductoId());

        if(inventarioResponse.getCantidad() < compraRequest.getCantidad()){
            log.warn("[generarCompra] Stock insuficiente - productoId: {}, stockDisponible: {}, stockSolicitado: {}",
                    compraRequest.getProductoId(), inventarioResponse.getCantidad(), compraRequest.getCantidad());
            throw new ProductoOutOfStock("Producto sin stock, máximo se pueden adquirir " + inventarioResponse.getCantidad());
        }

        String nombreProducto = productoService.obtenerNombreProducto(compraRequest.getProductoId());
        log.debug("[generarCompra] Nombre del producto obtenido: {} para productoId: {}",
                nombreProducto, compraRequest.getProductoId());

        // Actualizar inventario
        int nuevoStock = inventarioResponse.getCantidad() - compraRequest.getCantidad();
        InventarioRequest inventarioRequest = InventarioRequest.builder()
                .productoId(compraRequest.getProductoId())
                .cantidad(nuevoStock)
                .build();
        inventarioService.actualizarInventarioProducto(inventarioRequest);
        log.info("[generarCompra] Inventario actualizado - productoId: {}, stockAnterior: {}, stockNuevo: {}",
                compraRequest.getProductoId(), inventarioResponse.getCantidad(), nuevoStock);

        // Registrar compra
        HistorialCompra compra = compraRepository.save(HistorialCompra.builder()
                .productoId(compraRequest.getProductoId())
                .cantidad(compraRequest.getCantidad())
                .build());
        log.info("[generarCompra] Compra registrada - compraId: {}, productoId: {}, cantidad: {}",
                compra.getId(), compra.getProductoId(), compra.getCantidad());

        log.info("[generarCompra] Proceso completado - compraId: {}, productoId: {}, producto: {}, cantidad: {}",
                compra.getId(), compraRequest.getProductoId(), nombreProducto, compraRequest.getCantidad());

        return CompraResponse.fromEntity(compra, nombreProducto);
    }
}
