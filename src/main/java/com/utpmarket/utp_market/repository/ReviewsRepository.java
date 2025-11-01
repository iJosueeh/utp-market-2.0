package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    // Método que ya tenías - obtener reviews por producto
    List<Reviews> findByProductoId(Long productoId);

    // NUEVOS MÉTODOS para el módulo completo:

    // Obtener todas las reviews de un usuario (para el tab de perfil)
    @Query("SELECT r FROM Reviews r WHERE r.usuario.id = :usuarioId ORDER BY r.id DESC")
    List<Reviews> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Verificar si un usuario ya hizo review de un producto
    @Query("SELECT COUNT(r) > 0 FROM Reviews r WHERE r.usuario.id = :usuarioId AND r.producto.id = :productoId")
    boolean existsByUsuarioIdAndProductoId(@Param("usuarioId") Long usuarioId, @Param("productoId") Long productoId);

    // Calcular el promedio de puntaje de un producto
    @Query("SELECT AVG(r.puntaje) FROM Reviews r WHERE r.producto.id = :productoId")
    Double getPromedioPuntajePorProducto(@Param("productoId") Long productoId);

    // Contar cuántas reviews tiene un producto
    @Query("SELECT COUNT(r) FROM Reviews r WHERE r.producto.id = :productoId")
    Long countByProductoId(@Param("productoId") Long productoId);
}