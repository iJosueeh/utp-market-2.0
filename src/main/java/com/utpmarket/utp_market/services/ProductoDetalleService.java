package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.product.ProductoDetalleView;
import com.utpmarket.utp_market.repository.ProductoDetalleViewRepository;

import lombok.NonNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public ProductoDetalleView obtenerPorId(@NonNull Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de producto no encontrado"));
    }
}