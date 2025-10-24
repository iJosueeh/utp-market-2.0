package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoProductoRepository extends JpaRepository<EstadoProducto, Long> {
    EstadoProducto findByNombre(String nombre);
}
