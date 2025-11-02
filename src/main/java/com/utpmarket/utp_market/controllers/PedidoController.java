package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    //Mostrar el historial de pedidos del usuario
    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        List<Pedido> pedidos = pedidoService.obtenerHistorialPedidosPorUsuario(usuarioLogueado.getId());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("usuario", usuarioLogueado);

        return "pedidos/historial";
    }

    //Muestra los detalles de pedidos en específico
    @GetMapping("/detalle/{id}")
    public String mostrarDetallePedido(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            model.addAttribute("error", "No tienes permiso para ver este pedido");
            return "redirect:/pedidos/historial";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("usuario", usuarioLogueado);

        return "pedidos/detalle";
    }
}