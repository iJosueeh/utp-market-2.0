package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.cart.Carrito;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
}
