package com.microservicio.inventario.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.inventario.dto.CompraRequest;
import com.microservicio.inventario.dto.InventarioRequest;
import com.microservicio.inventario.dto.InventarioResponse;
import com.microservicio.inventario.entity.Inventario;
import com.microservicio.inventario.exception.ProductoNotFoundException;
import com.microservicio.inventario.repository.CompraRepository;
import com.microservicio.inventario.repository.InventarioRepository;
import com.microservicio.inventario.service.InventarioService;
import com.microservicio.inventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para los flujos de decrementar inventario y comprar productos.
 *
 * Usa H2 en memoria (perfil "test") y mockea ProductoService para evitar
 * llamadas HTTP al microservicio externo de productos.
 *
 * Inventario inicial por prueba (5 productos):
 *   productoId=1  cantidad=100
 *   productoId=2  cantidad=50
 *   productoId=3  cantidad=30
 *   productoId=4  cantidad=10
 *   productoId=5  cantidad=5
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlujoDecrementarComprarIntegrationTest {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY        = "test-api-key";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InventarioRepository inventarioRepository;
    @Autowired private CompraRepository compraRepository;
    @Autowired private InventarioService inventarioService;

    @MockBean
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        compraRepository.deleteAll();
        inventarioRepository.deleteAll();

        List.of(
                Inventario.builder().productoId(1L).cantidad(100).build(),
                Inventario.builder().productoId(2L).cantidad(50).build(),
                Inventario.builder().productoId(3L).cantidad(30).build(),
                Inventario.builder().productoId(4L).cantidad(10).build(),
                Inventario.builder().productoId(5L).cantidad(5).build()
        ).forEach(inventarioRepository::save);
    }

    // ================================================================
    // FLUJO: DECREMENTAR
    // ================================================================

    @Test
    void decrementar_stockDisponible_actualizaCantidadEnBD() {
        // productoId=1 stock=100; el campo 'cantidad' indica el stock RESULTANTE
        InventarioRequest request = InventarioRequest.builder()
                .productoId(1L)
                .cantidad(70)   // resultante: 100 - 30 = 70
                .build();

        InventarioResponse response = inventarioService.decrementarInventarioProducto(request);

        assertThat(response.getProductoId()).isEqualTo(1L);
        assertThat(response.getCantidad()).isEqualTo(70);

        Inventario enBD = inventarioRepository.obtenerInventario(1L).orElseThrow();
        assertThat(enBD.getCantidad()).isEqualTo(70);
        assertThat(enBD.getFechaModificacion()).isNotNull();
    }

    @Test
    void decrementar_todoElStock_dejaCantidadEnCero() {
        // productoId=3 stock=30; decrementamos todo el stock
        InventarioRequest request = InventarioRequest.builder()
                .productoId(3L)
                .cantidad(0)
                .build();

        InventarioResponse response = inventarioService.decrementarInventarioProducto(request);

        assertThat(response.getCantidad()).isEqualTo(0);
        assertThat(inventarioRepository.obtenerInventario(3L).orElseThrow().getCantidad()).isEqualTo(0);
    }

    @Test
    void decrementar_productoInexistente_lanzaProductoNotFoundException() {
        InventarioRequest request = InventarioRequest.builder()
                .productoId(99L)
                .cantidad(10)
                .build();

        assertThatThrownBy(() -> inventarioService.decrementarInventarioProducto(request))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void decrementar_multiplesProductos_actualizaCadaUnoSinAfectarLosOtros() {
        // Decrementamos producto 2 (50→25) y producto 4 (10→3)
        inventarioService.decrementarInventarioProducto(
                InventarioRequest.builder().productoId(2L).cantidad(25).build());
        inventarioService.decrementarInventarioProducto(
                InventarioRequest.builder().productoId(4L).cantidad(3).build());

        assertThat(inventarioRepository.obtenerInventario(2L).orElseThrow().getCantidad()).isEqualTo(25);
        assertThat(inventarioRepository.obtenerInventario(4L).orElseThrow().getCantidad()).isEqualTo(3);
        // Productos no modificados no deben cambiar
        assertThat(inventarioRepository.obtenerInventario(1L).orElseThrow().getCantidad()).isEqualTo(100);
        assertThat(inventarioRepository.obtenerInventario(5L).orElseThrow().getCantidad()).isEqualTo(5);
    }

    // ================================================================
    // FLUJO: COMPRAR
    // ================================================================

    @Test
    void comprar_stockSuficiente_retorna201YDecrementaInventario() throws Exception {
        when(productoService.obtenerNombreProducto(1L)).thenReturn("Laptop Gamer");

        CompraRequest request = CompraRequest.builder()
                .productoId(1L)
                .cantidad(15)
                .build();

        mockMvc.perform(post("/api/v1/compra")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.productoId").value(1))
                .andExpect(jsonPath("$.data.cantidad").value(15))
                .andExpect(jsonPath("$.data.nombreProducto").value("Laptop Gamer"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.fecha").isNotEmpty());

        // stock: 100 - 15 = 85
        assertThat(inventarioRepository.obtenerInventario(1L).orElseThrow().getCantidad()).isEqualTo(85);
        assertThat(compraRepository.count()).isEqualTo(1);
    }

    @Test
    void comprar_stockInsuficiente_retorna400YNoModificaInventario() throws Exception {
        // productoId=5 stock=5, se solicitan 10 → debe fallar
        CompraRequest request = CompraRequest.builder()
                .productoId(5L)
                .cantidad(10)
                .build();

        mockMvc.perform(post("/api/v1/compra")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                // El mensaje indica el máximo disponible (5)
                .andExpect(jsonPath("$.message").value(containsString("5")));

        // El inventario no debe haberse modificado
        assertThat(inventarioRepository.obtenerInventario(5L).orElseThrow().getCantidad()).isEqualTo(5);
        assertThat(compraRepository.count()).isEqualTo(0);
    }

    @Test
    void comprar_productoInexistente_retorna404() throws Exception {
        CompraRequest request = CompraRequest.builder()
                .productoId(99L)
                .cantidad(1)
                .build();

        mockMvc.perform(post("/api/v1/compra")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertThat(compraRepository.count()).isEqualTo(0);
    }

    @Test
    void comprar_sinApiKey_retorna401() throws Exception {
        CompraRequest request = CompraRequest.builder()
                .productoId(1L)
                .cantidad(5)
                .build();

        mockMvc.perform(post("/api/v1/compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        // No debe registrarse ninguna compra ni cambiar el inventario
        assertThat(compraRepository.count()).isEqualTo(0);
        assertThat(inventarioRepository.obtenerInventario(1L).orElseThrow().getCantidad()).isEqualTo(100);
    }

    @Test
    void comprar_loscincoProductos_verificaInventariosFinalesYHistorial() throws Exception {
        long[]   ids      = {1L,  2L,  3L,  4L, 5L};
        int[]    comprado = {20,  15,   5,   3,  2};
        int[]    esperado = {80,  35,  25,   7,  3};
        String[] nombres  = {"Prod A", "Prod B", "Prod C", "Prod D", "Prod E"};

        for (int i = 0; i < ids.length; i++) {
            when(productoService.obtenerNombreProducto(ids[i])).thenReturn(nombres[i]);

            mockMvc.perform(post("/api/v1/compra")
                            .header(API_KEY_HEADER, API_KEY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CompraRequest.builder()
                                            .productoId(ids[i])
                                            .cantidad(comprado[i])
                                            .build())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.nombreProducto").value(nombres[i]))
                    .andExpect(jsonPath("$.data.cantidad").value(comprado[i]));
        }

        // Verificar stock final de los 5 productos
        for (int i = 0; i < ids.length; i++) {
            assertThat(inventarioRepository.obtenerInventario(ids[i]).orElseThrow().getCantidad())
                    .as("Stock final del productoId=%d", ids[i])
                    .isEqualTo(esperado[i]);
        }

        // Debe haber 5 registros en el historial de compras
        assertThat(compraRepository.count()).isEqualTo(5);
    }
}
