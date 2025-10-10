package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.Cita;
import com.utpmarket.utp_market.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cita")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @PostMapping("/enviar")
    public String enviarFormulario(@ModelAttribute Cita cita, RedirectAttributes redirectAttributes) {
        try {
            citaService.guardarCita(cita);
            redirectAttributes.addFlashAttribute("successMessage", "¡Mensaje enviado y guardado con éxito! Gracias por contactarnos.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hubo un problema al enviar el mensaje. Inténtalo nuevamente.");
        }
        return "redirect:/#contacto";
    }
}