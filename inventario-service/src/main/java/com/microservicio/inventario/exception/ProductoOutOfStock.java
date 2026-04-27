package com.microservicio.inventario.exception;

public class ProductoOutOfStock extends RuntimeException {
    public ProductoOutOfStock(String message) {
        super(message);
    }
}
