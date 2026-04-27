package com.microservicio.producto.controller;

import com.microservicio.producto.dto.ResponseData;
import com.microservicio.producto.dto.ProductoRequest;
import com.microservicio.producto.dto.ProductoResponse;
import com.microservicio.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(
            summary = "Crear producto",
            description = "Crea un nuevo producto y registra su inventario inicial"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<ResponseData<ProductoResponse>> crearProducto(
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.crearProducto(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseData.created("Producto creado exitosamente", producto));
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Obtiene la información de un producto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ProductoResponse>> obtenerProducto(
            @PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(
                ResponseData.success("Producto encontrado", producto));
    }

    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene la lista completa de productos registrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<ResponseData<List<ProductoResponse>>> listarProductos() {
        List<ProductoResponse> productos = productoService.listarTodosLosProductos();
        return ResponseEntity.ok(
                ResponseData.success("Lista de productos", productos));
    }

    @Operation(
            summary = "Obtener nombre del producto",
            description = "Obtiene únicamente el nombre de un producto por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre del producto"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/obtener-nombre/{id}")
    public ResponseEntity<ResponseData<String>> obtenerNombreProducto(@PathVariable Long id) {
        String nombre = productoService.obtenerNombreProducto(id);
        return ResponseEntity.ok(
                ResponseData.success("Nombre del producto", nombre));
    }
}
