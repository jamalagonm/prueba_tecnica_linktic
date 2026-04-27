package com.microservicio.inventario.repository;

import com.microservicio.inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @Query(value = """
            SELECT * FROM inventario
            WHERE producto_id = :productoId;
            """, nativeQuery = true)
    Optional<Inventario> obtenerInventario(@Param("productoId") Long productoId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            update inventario
            set cantidad = :cantidad,
            fecha_modificacion = :fechaModificacion
            where producto_id = :productoId
            """, nativeQuery = true)
    Integer actualizarInventario(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad,
                                 @Param("fechaModificacion") LocalDateTime fechaModificacion);
}
