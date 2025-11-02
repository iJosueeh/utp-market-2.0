package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.Contacto;
import com.utpmarket.utp_market.services.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

    @Autowired
    private ContactoService contactoService;

    @PostMapping("/contacto")
    public String procesarContacto(@ModelAttribute Contacto contacto,
                                   RedirectAttributes redirectAttributes) {
        try {
            contactoService.procesarContacto(contacto);
            redirectAttributes.addFlashAttribute("mensaje", "Formulario enviado correctamente ✅");
        } catch (Exception e) {
            System.err.println("Error al procesar el contacto: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Hubo un problema al enviar el formulario. Inténtalo nuevamente.");
        }
        return "redirect:/";
    }

}