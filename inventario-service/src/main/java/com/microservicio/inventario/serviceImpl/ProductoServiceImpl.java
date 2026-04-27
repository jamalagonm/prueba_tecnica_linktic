package com.microservicio.inventario.serviceImpl;

import com.microservicio.inventario.dto.ResponseData;
import com.microservicio.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final RestClient restClient;

    public ProductoServiceImpl(@Value("${producto.service.url}") String url,
                               @Value("${app.security.api-key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }

    @Override
    public String obtenerNombreProducto(Long productoId) {
        log.info("[obtenerNombreProducto] Iniciando petición para obtener nombre del producto con ID: {}", productoId);
        try {
            ResponseData response = restClient.get()
                    .uri("/api/v1/productos/obtener-nombre/{id}", productoId)
                    .retrieve()
                    .body(ResponseData.class);

            if (response != null && response.getData() != null) {
                String nombre = (String) response.getData();
                log.debug("[obtenerNombreProducto] Nombre recuperado exitosamente para ID {}: {}", productoId, nombre);
                return nombre;
            } else {
                log.warn("[obtenerNombreProducto] La respuesta del servicio para el ID {} es nula o no contiene datos", productoId);
                return null;
            }

        } catch (Exception e) {
            log.error("[obtenerNombreProducto] Error al llamar al servicio de productos para el ID {}: {}", productoId, e.getMessage());
            throw e; // O manejar el error según tu lógica de negocio
        }
    }
}
