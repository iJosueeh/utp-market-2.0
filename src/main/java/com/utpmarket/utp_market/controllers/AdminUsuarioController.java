package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @GetMapping
    public String viewUsuarios(Model model) {
        model.addAttribute("activePage", "usuarios");
        // Lógica para Desarrollador 4
        return "admin/usuarios";
    }
}
