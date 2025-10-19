package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.ItemCarrito;
import com.utpmarket.utp_market.models.entity.Carrito;
import com.utpmarket.utp_market.models.entity.Usuario;
import com.utpmarket.utp_market.services.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public String mostrarCarrito(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        return "carrito/carrito";
    }

}
