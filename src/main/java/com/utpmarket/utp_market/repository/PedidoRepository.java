package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import com.utpmarket.utp_market.repository.VentasDiarias; // Import the moved interface
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    // Buscar todos los pedidos de un usuario, ordenados por fecha (más reciente primero)
    @Query("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId ORDER BY p.fecha_pedido DESC")
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(@Param("usuarioId") Long usuarioId);

    // Buscar un pedido específico por su número de pedido
    @Query("SELECT p FROM Pedido p WHERE p.numero_pedido = :numeroPedido")
    Pedido findByNumeroPedido(@Param("numeroPedido") String numeroPedido);

    // Buscar pedidos por usuario usando el objeto Usuario completo
    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario ORDER BY p.fecha_pedido DESC")
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(@Param("usuario") Usuario usuario);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE UPPER(p.estado.nombre) = 'COMPLETADO'")
    Double sumTotalVentas();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE UPPER(p.estado.nombre) = UPPER(:estadoNombre)")
    long countByEstadoNombre(@Param("estadoNombre") String estadoNombre);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.usuario JOIN FETCH p.estado ORDER BY p.fecha_pedido DESC")
    List<Pedido> findRecentOrders(Pageable pageable);

    @Query(value = "SELECT CAST(p.fecha_pedido AS DATE) as fecha, SUM(p.total) as total " +
            "FROM pedidos p " +
            "WHERE p.fecha_pedido >= CURRENT_DATE - 30 " +
            "GROUP BY CAST(p.fecha_pedido AS DATE) " +
            "ORDER BY fecha ASC", nativeQuery = true)
    List<VentasDiarias> findVentasDiariasUltimos30Dias();
}