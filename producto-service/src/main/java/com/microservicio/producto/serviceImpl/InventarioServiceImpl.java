package com.microservicio.producto.serviceImpl;

import com.microservicio.producto.entity.Producto;
import com.microservicio.producto.service.InventarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Slf4j
public class InventarioServiceImpl implements InventarioService {

    private final RestClient restClient;

    public InventarioServiceImpl(@Value("${inventario.service.url}") String url,
                                 @Value("${app.security.api-key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }

    @Override
    public void crearProductoInventario(Producto producto, Integer cantidad) {
        log.info("[crearProductoInventario] Inicio - producto ID: {}, cantidad: {}",
                producto.getId(), cantidad);

        Map<String, Object> body = Map.of(
                "productoId", producto.getId(),
                "cantidad", cantidad
        );

        log.debug("[crearProductoInventario] Enviando request al servicio inventario - body: {}", body);

        try {
            String response = restClient.post()
                    .uri("/api/v1/inventario")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            log.debug("[crearProductoInventario] Respuesta del servicio inventario: {}", response);
            log.info("[crearProductoInventario] Inventario creado exitosamente - producto ID: {}, cantidad: {}",
                    producto.getId(), cantidad);
        } catch (Exception e) {
            log.error("[crearProductoInventario] Error al comunicarse con el servicio inventario - producto ID: {}",
                    producto.getId(), e);
            throw e;
        }
    }
}
