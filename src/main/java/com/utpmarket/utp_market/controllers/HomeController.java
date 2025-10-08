package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.Contacto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Model model) {
        return "index";
    }

}