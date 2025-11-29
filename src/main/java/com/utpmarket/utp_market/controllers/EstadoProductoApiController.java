package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.EstadoProducto;
import com.utpmarket.utp_market.services.EstadoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/estados-producto")
public class EstadoProductoApiController {

    @Autowired
    private EstadoProductoService estadoProductoService;

    @GetMapping
    public ResponseEntity<List<EstadoProducto>> getEstadosProducto() {
        return ResponseEntity.ok(estadoProductoService.findAll());
    }
}
