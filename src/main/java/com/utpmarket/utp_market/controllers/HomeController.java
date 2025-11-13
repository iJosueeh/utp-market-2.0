package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String inicio(Model model, Principal principal) {
        if (principal != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(principal.getName());
            usuarioOpt.ifPresent(usuario -> {
                model.addAttribute("nombreCompleto", usuario.getNombreCompleto());
                model.addAttribute("correo", usuario.getEmail());
            });
        }
        return "index";
    }
}
