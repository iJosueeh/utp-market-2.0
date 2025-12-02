package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import com.utpmarket.utp_market.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaApiController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getCategorias() {
        return ResponseEntity.ok(categoriaService.findAllCategoriasWithProductCount());
    }
}
