package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReporteController {

    @GetMapping
    public String viewReportes(Model model) {
        model.addAttribute("activePage", "reportes");
        // Lógica para Desarrollador 5
        return "admin/reportes";
    }
}
