package com.microservicio.inventario.controller;

import com.microservicio.inventario.dto.*;
import com.microservicio.inventario.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compra")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @Operation(
            summary = "Registrar compra",
            description = "Registra una nueva compra, valida stock disponible y actualiza el inventario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o error de validación"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("")
    public ResponseEntity<ResponseData<CompraResponse>> crearCompra(
            @Valid @RequestBody CompraRequest request) {
        CompraResponse compra = compraService.generarCompra(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseData.created("Compra registrada exitosamente", compra));
    }

}
