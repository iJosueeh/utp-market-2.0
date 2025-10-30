package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.order.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
    EstadoPedido findByNombre(String nombre);
}
