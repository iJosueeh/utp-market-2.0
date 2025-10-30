package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.cart.ItemsCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsCarritoRepository extends JpaRepository<ItemsCarrito, Long> {
}
