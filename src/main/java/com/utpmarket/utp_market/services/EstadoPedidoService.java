package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.order.EstadoPedido;
import com.utpmarket.utp_market.repository.EstadoPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstadoPedidoService {

    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    @Transactional(readOnly = true)
    public List<EstadoPedido> findAll() {
        return estadoPedidoRepository.findAll();
    }
}
