package com.microservicio.producto.service;

import com.microservicio.producto.dto.ProductoRequest;
import com.microservicio.producto.dto.ProductoResponse;
import com.microservicio.producto.entity.Producto;
import com.microservicio.producto.exception.ProductoNotFoundException;
import com.microservicio.producto.repository.ProductoRepository;
import com.microservicio.producto.service.InventarioService;
import com.microservicio.producto.serviceImpl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private ProductoRequest request;
    private Producto producto;

    @BeforeEach
    void setUp() {
        request = ProductoRequest.builder()
                .nombre("Laptop")
                .descripcion("Laptop gamer")
                .precio(new BigDecimal("2500.00"))
                .cantidad(10)
                .build();

        producto = Producto.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop gamer")
                .precio(new BigDecimal("2500.00"))
                .build();
    }

    // ─── crearProducto ────────────────────────────────────────────────────────

    @Test
    void crearProducto_cuandoInventarioOk_retornaResponse() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        doNothing().when(inventarioService).crearProductoInventario(any(Producto.class), eq(10));

        ProductoResponse response = productoService.crearProducto(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Laptop");
        verify(inventarioService).crearProductoInventario(producto, 10);
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void crearProducto_cuandoInventarioFalla_eliminaProductoYLanzaExcepcion() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        doThrow(new RuntimeException("Servicio no disponible"))
                .when(inventarioService).crearProductoInventario(any(Producto.class), eq(10));

        assertThatThrownBy(() -> productoService.crearProducto(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo crear el inventario");

        verify(productoRepository).deleteById(1L);
    }

    // ─── guardarProducto ──────────────────────────────────────────────────────

    @Test
    void guardarProducto_mapaCamposCorrectamente() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto result = productoService.guardarProducto(request);

        assertThat(result.getNombre()).isEqualTo("Laptop");
        assertThat(result.getDescripcion()).isEqualTo("Laptop gamer");
        assertThat(result.getPrecio()).isEqualByComparingTo("2500.00");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void guardarProducto_retornaEntidadPersistida() {
        Producto persistido = Producto.builder()
                .id(42L)
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .precio(new BigDecimal("800.00"))
                .build();
        ProductoRequest otroRequest = ProductoRequest.builder()
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .precio(new BigDecimal("800.00"))
                .cantidad(5)
                .build();
        when(productoRepository.save(any(Producto.class))).thenReturn(persistido);

        Producto result = productoService.guardarProducto(otroRequest);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getNombre()).isEqualTo("Monitor");
    }

    // ─── obtenerProductoPorId ─────────────────────────────────────────────────

    @Test
    void obtenerProductoPorId_cuandoExiste_retornaResponse() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponse response = productoService.obtenerProductoPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Laptop");
        assertThat(response.getPrecio()).isEqualByComparingTo("2500.00");
    }

    @Test
    void obtenerProductoPorId_cuandoNoExiste_lanzaProductoNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerProductoPorId(99L))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── listarTodosLosProductos ──────────────────────────────────────────────

    @Test
    void listarTodosLosProductos_cuandoHayProductos_retornaLista() {
        Producto segundo = Producto.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalámbrico")
                .precio(new BigDecimal("50.00"))
                .build();
        when(productoRepository.findAll()).thenReturn(List.of(producto, segundo));

        List<ProductoResponse> result = productoService.listarTodosLosProductos();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductoResponse::getNombre)
                .containsExactly("Laptop", "Mouse");
    }

    @Test
    void listarTodosLosProductos_cuandoNoHayProductos_retornaListaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());

        List<ProductoResponse> result = productoService.listarTodosLosProductos();

        assertThat(result).isEmpty();
    }

    // ─── obtenerNombreProducto ────────────────────────────────────────────────

    @Test
    void obtenerNombreProducto_cuandoExiste_retornaNombre() {
        when(productoRepository.obtenerNombre(1L)).thenReturn("Laptop");

        String nombre = productoService.obtenerNombreProducto(1L);

        assertThat(nombre).isEqualTo("Laptop");
    }

    @Test
    void obtenerNombreProducto_cuandoNoExiste_retornaNull() {
        when(productoRepository.obtenerNombre(99L)).thenReturn(null);

        String nombre = productoService.obtenerNombreProducto(99L);

        assertThat(nombre).isNull();
        verify(productoRepository).obtenerNombre(99L);
    }
}