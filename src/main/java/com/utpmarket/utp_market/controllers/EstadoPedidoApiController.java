package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.EstadoPedidoDTO;
import com.utpmarket.utp_market.services.EstadoPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/pedidos/estados")
public class EstadoPedidoApiController {

    @Autowired
    private EstadoPedidoService estadoPedidoService;

    @GetMapping
    public ResponseEntity<List<EstadoPedidoDTO>> getAllEstadosPedidos() {
        List<EstadoPedidoDTO> estados = estadoPedidoService.findAll()
                                                    .stream()
                                                    .map(estado -> new EstadoPedidoDTO(estado.getId(), estado.getNombre()))
                                                    .collect(Collectors.toList());
        return ResponseEntity.ok(estados);
    }
}
