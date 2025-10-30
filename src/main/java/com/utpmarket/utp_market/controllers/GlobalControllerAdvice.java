package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import com.utpmarket.utp_market.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoriaService categoriaService;

    @ModelAttribute("categoriasGlobal")
    public List<CategoriaDTO> getCategoriasGlobal() {
        return categoriaService.findAllCategoriasWithProductCount();
    }
}
