package com.microservicio.inventario.repository;

import com.microservicio.inventario.entity.HistorialCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<HistorialCompra, Long> {
}
