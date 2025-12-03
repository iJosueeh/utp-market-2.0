package com.utpmarket.utp_market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model; // Corrected import

import com.utpmarket.utp_market.services.PedidoService;
import com.utpmarket.utp_market.models.dto.PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String viewPedidos(Model model) {
        model.addAttribute("activePage", "pedidos");
        // Lógica para Desarrollador 2
        return "admin/pedidos";
    }

    @GetMapping("/api/list")
    public ResponseEntity<Page<PedidoDTO>> getPedidos(
            Pageable pageable,
            @RequestParam(name = "estadoId", required = false) Long estadoId,
            @RequestParam(name = "fechaInicio", required = false) String fechaInicioStr,
            @RequestParam(name = "fechaFin", required = false) String fechaFinStr) {

        System.out.println("AdminPedidoController - getPedidos called with:");
        System.out.println("  Pageable: " + pageable);
        System.out.println("  estadoId (param): " + estadoId);
        System.out.println("  fechaInicioStr (param): " + fechaInicioStr);
        System.out.println("  fechaFinStr (param): " + fechaFinStr);

        Timestamp fechaInicio = null;
        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
            try {
                fechaInicio = Timestamp.valueOf(LocalDate.parse(fechaInicioStr).atStartOfDay());
                System.out.println("  Parsed fechaInicio: " + fechaInicio);
            } catch (DateTimeParseException e) {
                // Log error or handle invalid date format
                System.err.println("Invalid fechaInicio format: " + fechaInicioStr);
            }
        }

        Timestamp fechaFin = null;
        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            try {
                fechaFin = Timestamp.valueOf(LocalDate.parse(fechaFinStr).atTime(23, 59, 59));
                System.out.println("  Parsed fechaFin: " + fechaFin);
            } catch (DateTimeParseException e) {
                // Log error or handle invalid date format
                System.err.println("Invalid fechaFin format: " + fechaFinStr);
            }
        }

        Page<PedidoDTO> pedidos = pedidoService.obtenerPedidosPaginadosYFiltrados(pageable, estadoId, fechaInicio, fechaFin);
        System.out.println("  Returning " + pedidos.getTotalElements() + " pedidos.");
        return ResponseEntity.ok(pedidos);
    }
}