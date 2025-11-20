package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @GetMapping
    public String viewProductos(Model model) {
        model.addAttribute("activePage", "productos");
        // Lógica para Desarrollador 3
        return "admin/productos";
    }
}
