package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    @GetMapping
    public String viewPedidos(Model model) {
        model.addAttribute("activePage", "pedidos");
        // Lógica para Desarrollador 2
        return "admin/pedidos";
    }
}
