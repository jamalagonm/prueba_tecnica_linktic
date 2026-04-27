package com.microservicio.inventario.service;

import com.microservicio.inventario.dto.CompraRequest;
import com.microservicio.inventario.dto.CompraResponse;
import com.microservicio.inventario.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class CompraServiceImplTest {

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private CompraService compraService;

    private CompraRequest compraRequest;
    private CompraResponse compraResponse;

    @BeforeEach
    void setUp(){
        compraRequest = CompraRequest.builder()
                .productoId(10L)
                .cantidad(25)
                .build();

        compraResponse = CompraResponse.builder()
                .id(1L)
                .productoId(10L)
                .cantidad(20)
                .nombreProducto("IPAD")
                .fecha(LocalDateTime.now())
                .build();
    }
}
