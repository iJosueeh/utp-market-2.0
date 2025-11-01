package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar todos los pedidos de un usuario, ordenados por fecha (más reciente primero)
    @Query("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId ORDER BY p.fecha_pedido DESC")
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(@Param("usuarioId") Long usuarioId);

    // Buscar un pedido específico por su número de pedido
    @Query("SELECT p FROM Pedido p WHERE p.numero_pedido = :numeroPedido")
    Pedido findByNumeroPedido(@Param("numeroPedido") String numeroPedido);

    // Buscar pedidos por usuario usando el objeto Usuario completo
    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario ORDER BY p.fecha_pedido DESC")
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(@Param("usuario") Usuario usuario);
}