package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.CarritoService;
import com.utpmarket.utp_market.services.PedidoService;
import com.utpmarket.utp_market.services.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
        double subtotal = carritoService.calcularSubtotal(usuario.getId());
        double total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);

        return "carito/carrito";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId, @RequestParam int cantidad, HttpSession session, RedirectAttributes redirectAttributes, @RequestHeader(value = "Referer", required = false) String referer) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            carritoService.agregarProducto(usuario.getId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/eliminar/{itemId}")
    public String eliminarDelCarrito(@PathVariable Long itemId, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        carritoService.eliminarProducto(usuario.getId(), itemId);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");

        return "redirect:/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
        double subtotal = carritoService.calcularSubtotal(usuario.getId());
        double total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);

        return "carito/checkout";
    }

    @PostMapping("/realizar-pago")
    public String realizarPago(@RequestParam Long metodoPagoId,
                               @RequestParam String calle,
                               @RequestParam String distrito,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            Direccion direccionEnvio = new Direccion(calle, distrito);
            Pedido pedido = pedidoService.crearPedido(usuario.getId(), metodoPagoId, direccionEnvio);

            redirectAttributes.addFlashAttribute("userEmail", usuario.getEmail());
            redirectAttributes.addFlashAttribute("orderNumber", pedido.getNumero_pedido());
            return "redirect:/carrito/pedido-confirmacion";

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito/checkout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago. Intenta nuevamente.");
            return "redirect:/carrito/checkout";
        }
    }
}