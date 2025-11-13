package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaService categoriaService;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElse(null);

            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("nombreCompletoUsuario", usuario.getNombre() + " " + usuario.getApellido());
            }
        }
        model.addAttribute("categoriasGlobal", categoriaService.findAllCategoriasWithProductCount());
    }
}