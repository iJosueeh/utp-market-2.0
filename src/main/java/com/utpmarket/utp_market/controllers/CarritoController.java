package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.CarritoService;
import com.utpmarket.utp_market.services.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

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
            return "redirect:/"; // Fallback to home page if Referer is not available
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
    public String realizarPago(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());

        for (CarritoItemDTO item : carritoItems) {
            productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
        }

        carritoService.limpiarCarrito(usuario.getId());

        Random random = new Random();
        int year = Year.now().getValue();
        int randomNum = 1000 + random.nextInt(9000);
        String orderNumber = "ORD-" + year + "-" + randomNum;

        model.addAttribute("userEmail", usuario.getEmail());
        model.addAttribute("orderNumber", orderNumber);

        return "carito/pedido-confirmacion";
    }
}
