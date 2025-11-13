package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Mostrar el historial de pedidos del usuario
    @GetMapping("/historial")
    @PreAuthorize("isAuthenticated()")
    public String mostrarHistorial(Principal principal, Model model) {
        Usuario usuarioLogueado = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        List<Pedido> pedidos = pedidoService.obtenerHistorialPedidosPorUsuario(usuarioLogueado.getId());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("usuario", usuarioLogueado);

        return "pedidos/historial";
    }

    //Muestra los detalles de pedidos en específico
    @GetMapping("/detalle/{id}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarDetallePedido(@PathVariable Long id, Principal principal, Model model) {
        Usuario usuarioLogueado = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            // Lanzar una excepción de acceso denegado es más apropiado en un entorno de Spring Security
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este pedido");
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("usuario", usuarioLogueado);

        return "pedidos/detalle";
    }
}