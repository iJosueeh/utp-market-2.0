package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.repository.PedidoRepository;
import com.utpmarket.utp_market.repository.VentasDiarias;
import com.utpmarket.utp_market.services.ReporteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String viewReportes(Model model) {
        model.addAttribute("activePage", "reportes");
        return "admin/reportes";
    }

    @GetMapping("/api/ventas-semanales")
    @ResponseBody
    public Map<String, Object> getVentas(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fin
    ) {

        LocalDate fechaInicio = (inicio != null && !inicio.isEmpty()) ? LocalDate.parse(inicio) : null;
        LocalDate fechaFin = (fin != null && !fin.isEmpty()) ? LocalDate.parse(fin) : null;

        List<VentasDiarias> ventas = reporteService.getVentasPorRango(fechaInicio, fechaFin);

        // Convertir para ChartJS
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        List<String> labels = ventas.stream()
                .map(v -> v.getFecha().format(formatter))
                .toList();

        List<Double> data = ventas.stream()
                .map(VentasDiarias::getTotal)
                .toList();

        return Map.of(
                "labels", labels,
                "data", data
        );
    }
}