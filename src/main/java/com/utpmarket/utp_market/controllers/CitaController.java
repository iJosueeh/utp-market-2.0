package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cita")
public class CitaController {

    @PostMapping("/enviar")
    public String enviarFormulario(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            Model model) {

        try {
            model.addAttribute("successMessage", "¡Mensaje enviado con éxito! Gracias por contactarnos.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Hubo un problema al enviar el mensaje. Inténtalo nuevamente.");
        }

        return "index";
    }

}
