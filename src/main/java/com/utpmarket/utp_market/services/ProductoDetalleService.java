package com.utpmarket.utp_market.services;


import com.utpmarket.utp_market.models.entity.product.ProductoDetalleView;
import com.utpmarket.utp_market.repository.ProductoDetalleViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoDetalleService {

    private final ProductoDetalleViewRepository repository;

    public ProductoDetalleService(ProductoDetalleViewRepository repository) {
        this.repository = repository;
    }

    public List<ProductoDetalleView> listarTodos() {
        return repository.findAll();
    }

    public ProductoDetalleView obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
    }
}
