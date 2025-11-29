package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.product.EstadoProducto;
import com.utpmarket.utp_market.repository.EstadoProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoProductoService {

    @Autowired
    private EstadoProductoRepository estadoProductoRepository;

    public List<EstadoProducto> findAll() {
        return estadoProductoRepository.findAll();
    }
}
