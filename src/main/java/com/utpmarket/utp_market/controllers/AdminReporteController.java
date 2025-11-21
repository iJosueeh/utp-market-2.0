package com.utpmarket.utp_market.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utpmarket.utp_market.repository.PedidoRepository;
import com.utpmarket.utp_market.repository.VentasDiarias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReporteController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String viewReportes(Model model) {
        model.addAttribute("activePage", "reportes");
        // Lógica para Desarrollador 5
        return "admin/reportes";
    }

    @GetMapping("/api/ventas-semanales")
    @ResponseBody
    public Map<String, Object> getVentasSemanales() {
        List<VentasDiarias> ventas = pedidoRepository.findVentasDiariasUltimos30Dias();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> labels = ventas.stream()
                .map(v -> v.getFecha().format(formatter))
                .toList();

        List<Double> data = ventas.stream()
                .map(VentasDiarias::getTotal)
                .toList();

        return Map.of("labels", labels, "data", data);
    }
}