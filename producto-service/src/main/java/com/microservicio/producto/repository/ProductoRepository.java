package com.microservicio.producto.repository;

import com.microservicio.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query(value = """
                SELECT nombre FROM productos
                WHERE id = :productoId;
            """, nativeQuery = true)
    String obtenerNombre(@Param("productoId") Long productoId);

}
