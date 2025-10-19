package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.Carrito;
import com.utpmarket.utp_market.models.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long>  {
    List<Carrito> findByCliente(Usuario cliente);
}
