package com.microservicio.inventario.controller;

import com.microservicio.inventario.dto.InventarioResponse;
import com.microservicio.inventario.dto.ResponseData;
import com.microservicio.inventario.dto.InventarioRequest;
import com.microservicio.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(
            summary = "Obtener inventario por producto",
            description = "Consulta la cantidad disponible en inventario de un producto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{productoId}")
    public ResponseEntity<ResponseData<InventarioResponse>> obtenerProducto(@PathVariable Long productoId) {
        InventarioResponse inventario = inventarioService.obtenerProducto(productoId);
        return ResponseEntity.ok(
                ResponseData.success("Inventario encontrado", inventario));
    }


    @Operation(
            summary = "Crear inventario",
            description = "Registra un nuevo inventario para un producto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("")
    public ResponseEntity<ResponseData<InventarioResponse>> crearInventario(
            @Valid @RequestBody InventarioRequest request) {
        InventarioResponse inventario = inventarioService.crearInventarioProducto(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseData.created("Inventario creado exitosamente", inventario));
    }


    @Operation(
            summary = "Actualizar inventario",
            description = "Actualiza la cantidad disponible en inventario de un producto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("")
    public ResponseEntity<ResponseData<InventarioResponse>> actualizarInventario(
            @Valid @RequestBody InventarioRequest request) {
        InventarioResponse inventario = inventarioService.actualizarInventarioProducto(request);
        return ResponseEntity.ok(
                ResponseData.success("Inventario actualizado exitosamente", inventario));
    }
}
