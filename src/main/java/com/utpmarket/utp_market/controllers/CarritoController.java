package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.CarritoService;
import com.utpmarket.utp_market.services.PedidoService;
import com.utpmarket.utp_market.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StripeService stripeService;

    private Usuario getUsuarioFromPrincipal(Principal principal) {
        return usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en la base de datos"));
    }

    @GetMapping
    public String verCarrito(Principal principal, Model model) {
        // Permitir acceso sin autenticación, mostrar carrito vacío
        if (principal == null) {
            model.addAttribute("carritoItems", List.of());
            model.addAttribute("subtotal", 0.0);
            model.addAttribute("total", 0.0);
            model.addAttribute("requiresLogin", true);
            return "carito/carrito";
        }

        Usuario usuario = getUsuarioFromPrincipal(principal);
        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
        double subtotal = carritoService.calcularSubtotal(usuario.getId());
        double total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);
        model.addAttribute("requiresLogin", false);

        return "carito/carrito";
    }

    @PostMapping("/agregar")
    @PreAuthorize("isAuthenticated()")
    public String agregarAlCarrito(@RequestParam Long productoId, @RequestParam int cantidad, Principal principal,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.agregarProducto(usuario.getId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar-cantidad")
    @PreAuthorize("isAuthenticated()")
    public String actualizarCantidadItem(@RequestParam Long itemId,
            @RequestParam int cantidad,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.actualizarCantidadItem(usuario.getId(), itemId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cantidad");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar-cantidad-ajax")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> actualizarCantidadItemAjax(@RequestParam Long itemId,
            @RequestParam int cantidad,
            Principal principal) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.actualizarCantidadItem(usuario.getId(), itemId, cantidad);

            List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
            double subtotal = carritoService.calcularSubtotal(usuario.getId());
            double total = carritoService.calcularTotal(usuario.getId());

            double itemSubtotal = carritoItems.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .map(CarritoItemDTO::getSubtotal)
                    .orElse(0.0);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "itemSubtotal", itemSubtotal,
                    "subtotal", subtotal,
                    "total", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/eliminar/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public String eliminarDelCarrito(@PathVariable Long itemId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.eliminarProducto(usuario.getId(), itemId);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar-ajax/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> eliminarDelCarritoAjax(@PathVariable Long itemId, Principal principal) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.eliminarProducto(usuario.getId(), itemId);

            List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
            double subtotal = carritoService.calcularSubtotal(usuario.getId());
            double total = carritoService.calcularTotal(usuario.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "carritoItems", carritoItems,
                    "subtotal", subtotal,
                    "total", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String checkout(Principal principal, Model model) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
        double subtotal = carritoService.calcularSubtotal(usuario.getId());
        double total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);

        return "carito/checkout";
    }

    @GetMapping("/pedido-confirmacion")
    public String pedidoConfirmacion() {
        return "carito/pedido-confirmacion";
    }

    @PostMapping("/realizar-pago")
    @PreAuthorize("isAuthenticated()")
    public String realizarPago(@RequestParam Long metodoPagoId,
            @RequestParam String calle,
            @RequestParam String distrito,
            @RequestParam(required = false) String stripeToken,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            Direccion direccionEnvio = new Direccion(calle, distrito);
            String transactionId = null;

            // Si es tarjeta (ID 2 o 3, asumiendo que estos son los IDs para tarjeta)
            // Deberías verificar esto contra tu base de datos o constantes
            if (metodoPagoId == 2 || metodoPagoId == 3) {
                if (stripeToken == null || stripeToken.isEmpty()) {
                    throw new IllegalArgumentException("Token de pago no recibido.");
                }
                double total = carritoService.calcularTotal(usuario.getId());
                // Use createAndConfirmPayment which returns a PaymentIntent
                transactionId = stripeService.createAndConfirmPayment(stripeToken, total, "PEN", usuario.getEmail())
                        .getId();
            }

            Pedido pedido = pedidoService.crearPedido(usuario.getId(), metodoPagoId, direccionEnvio, transactionId);

            redirectAttributes.addFlashAttribute("userEmail", usuario.getEmail());
            redirectAttributes.addFlashAttribute("orderNumber", pedido.getNumero_pedido());
            return "redirect:/carrito/pedido-confirmacion";

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito/checkout";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/carrito/checkout";
        }
    }
}