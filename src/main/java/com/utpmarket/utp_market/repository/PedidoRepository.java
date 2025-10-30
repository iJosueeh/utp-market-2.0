package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
