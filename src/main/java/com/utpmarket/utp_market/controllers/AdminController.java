package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.repository.PedidoRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");

        long totalUsuarios = usuarioRepository.count();
        double totalVentas = pedidoRepository.sumTotalVentas();
        long pedidosPendientes = pedidoRepository.countByEstadoNombre("PENDIENTE");
        long totalProductos = productoRepository.count(); // Necesitarás el ProductoRepository

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("pedidosRecientes", pedidoRepository.findRecentOrders(PageRequest.of(0, 5)));

        return "admin/dashboard";
    }
}