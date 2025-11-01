package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Obtiene el historial de pedidos de un usuario
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);
    }

    // Obtiene un pedido por su ID
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    // Obtiene un pedido por su numero de pedido
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorNumero(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    // Obtener todos los pedidos
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    // Guarda y Actualiza
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}