package com.microservicio.inventario.service;

import com.microservicio.inventario.dto.InventarioRequest;
import com.microservicio.inventario.dto.InventarioResponse;
import com.microservicio.inventario.entity.Inventario;
import com.microservicio.inventario.exception.ProductoAlreadyExistsException;
import com.microservicio.inventario.exception.ProductoNotFoundException;
import com.microservicio.inventario.repository.InventarioRepository;
import com.microservicio.inventario.serviceImpl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventario;
    private InventarioRequest request;

    @BeforeEach
    void setUp() {
        inventario = Inventario.builder()
                .id(1L)
                .productoId(10L)
                .cantidad(100)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        request = InventarioRequest.builder()
                .productoId(10L)
                .cantidad(50)
                .build();
    }

    // -------------------------------------------------------------------------
    // obtenerProducto
    // -------------------------------------------------------------------------

    @Test
    void obtenerProductoExistente() {
        when(inventarioRepository.obtenerInventario(10L)).thenReturn(Optional.of(inventario));

        InventarioResponse response = inventarioService.obtenerProducto(10L);

        assertThat(response.getProductoId()).isEqualTo(10L);
        assertThat(response.getCantidad()).isEqualTo(100);
        verify(inventarioRepository).obtenerInventario(10L);
    }

    @Test
    void obtenerProductoNoExistente() {
        when(inventarioRepository.obtenerInventario(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.obtenerProducto(99L))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // crearInventarioProducto
    // -------------------------------------------------------------------------

    @Test
    void crearInventarioProducto_cuandoNoExiste_guardaYRetornaResponse() {
        when(inventarioRepository.obtenerInventario(10L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        InventarioResponse response = inventarioService.crearInventarioProducto(request);

        assertThat(response.getProductoId()).isEqualTo(10L);
        assertThat(response.getId()).isEqualTo(1L);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void crearInventarioProducto_cuandoYaExiste_lanzaProductoAlreadyExistsException() {
        when(inventarioRepository.obtenerInventario(10L)).thenReturn(Optional.of(inventario));

        assertThatThrownBy(() -> inventarioService.crearInventarioProducto(request))
                .isInstanceOf(ProductoAlreadyExistsException.class)
                .hasMessageContaining("ya existente");

        verify(inventarioRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // actualizarInventarioProducto
    // -------------------------------------------------------------------------

    @Test
    void actualizarInventarioProducto_cuandoExiste_actualizaYRetornaResponse() {
        when(inventarioRepository.obtenerInventario(10L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.actualizarInventario(eq(10L), eq(50), any(LocalDateTime.class)))
                .thenReturn(1);

        InventarioResponse response = inventarioService.actualizarInventarioProducto(request);

        assertThat(response.getProductoId()).isEqualTo(10L);
        assertThat(response.getCantidad()).isEqualTo(50);
        verify(inventarioRepository).actualizarInventario(eq(10L), eq(50), any(LocalDateTime.class));
    }

    @Test
    void actualizarInventarioProducto_cuandoNoExiste_lanzaProductoNotFoundException() {
        InventarioRequest requestInexistente = InventarioRequest.builder()
                .productoId(99L)
                .cantidad(20)
                .build();
        when(inventarioRepository.obtenerInventario(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.actualizarInventarioProducto(requestInexistente))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");

        verify(inventarioRepository, never()).actualizarInventario(any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // decrementarInventarioProducto
    // -------------------------------------------------------------------------

    @Test
    void decrementarInventarioProducto_cuandoExiste_decrementaYRetornaStockResultante() {
        when(inventarioRepository.obtenerInventario(10L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.actualizarInventario(eq(10L), eq(50), any(LocalDateTime.class)))
                .thenReturn(1);

        InventarioResponse response = inventarioService.decrementarInventarioProducto(request);

        assertThat(response.getCantidad()).isEqualTo(50);
        verify(inventarioRepository).actualizarInventario(eq(10L), eq(50), any(LocalDateTime.class));
    }

    @Test
    void decrementarInventarioProducto_cuandoNoExiste_lanzaProductoNotFoundException() {
        InventarioRequest requestInexistente = InventarioRequest.builder()
                .productoId(55L)
                .cantidad(10)
                .build();
        when(inventarioRepository.obtenerInventario(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.decrementarInventarioProducto(requestInexistente))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("55");
    }
}