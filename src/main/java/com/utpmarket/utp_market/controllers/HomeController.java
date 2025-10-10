package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
            model.addAttribute("correo", usuario.getEmail());
        }
        return "index";
    }

}
