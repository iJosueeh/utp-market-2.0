package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.user.Favorito;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.entity.product.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);
    List<Favorito> findAllByUsuario(Usuario usuario);
}