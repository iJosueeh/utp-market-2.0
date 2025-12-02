package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.Categoria;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    Long countByCategoria(Categoria categoria);

    Long countByVendedor(Usuario vendedor);

    List<Producto> findByStockGreaterThan(Integer stock);

    List<Producto> findByNombreContainingIgnoreCase(String nombre); // Nuevo método de búsqueda

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.estudianteDetalles WHERE u.email = :email")
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.imagenes WHERE p.categoria.id = :categoriaId AND p.id <> :productoId")
    List<Producto> findRelatedProducts(@Param("categoriaId") Long categoriaId,
                                       @Param("productoId") Long productoId);

}