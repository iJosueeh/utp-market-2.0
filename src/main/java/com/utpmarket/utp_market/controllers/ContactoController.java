package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

    @PostMapping("/contacto")
    public String procesarContacto(@RequestParam String categoria,
                                   @RequestParam String nombre,
                                   @RequestParam String correo,
                                   @RequestParam String mensaje,
                                   RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("mensaje", "Formulario enviado correctamente ✅");
        return "redirect:/";
    }

}