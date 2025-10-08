package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @GetMapping("/about-us")
    public String aboutUs() {
        return "pages/about";
    }

    @GetMapping("/help")
    public String help() {
        return "pages/help";
    }

    @GetMapping("/sedes")
    public String sedes(Model model) {

        List<Map<String, String>> sedes = List.of(
                Map.of(
                        "nombre", "Sede Norte",
                        "direccion", "Panamericana Norte, Av. Alfredo Mendiola 6377, Los Olivos 15306",
                        "telefono", "970804148",
                        "mapa", "https://www.google.com/maps/place/Panamericana+Norte+6377,+Los+Olivos+15306",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/campus-los-olivos-utp.webp"
                ),
                Map.of(
                        "nombre", "Sede Lima Centro",
                        "direccion", "Avenida Petit Thouars 116, Lima 15046",
                        "telefono", "(01) 3159600",
                        "mapa", "https://www.google.com/maps/place/Avenida+Petit+Thouars+116,+Lima+15046",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/utp-lima-centro.webp"
                ),
                Map.of(
                        "nombre", "Sede Lima Sur",
                        "direccion", "Ctra. Panamericana Sur km 16, Villa El Salvador 15842",
                        "telefono", "(01) 3159600",
                        "mapa", "https://www.google.com/maps/place/Panamericana+Sur+Km+16,+Villa+El+Salvador+15842",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/LimaSur-.webp"
                ),
                Map.of(
                        "nombre", "Sede San Juan de Lurigancho",
                        "direccion", "Av. Wiesse 571, San Juan de Lurigancho 15434",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/REhYfwjyYBLGhErc6",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/SJL-.webp"
                ),
                Map.of(
                        "nombre", "Sede Ate",
                        "direccion", "Av. Carretera Central 123, Ate, Lima",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/m6p3aiWzAuyf8mLu7",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/utp-sede-ate.webp"
                )
        );

        model.addAttribute("sedes", sedes);

        return "pages/sedes";
    }

}