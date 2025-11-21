package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.CarritoService;
import com.utpmarket.utp_market.services.PedidoService;
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
@PreAuthorize("isAuthenticated()")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioFromPrincipal(Principal principal) {
        return usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en la base de datos"));
    }

    @GetMapping
    public String verCarrito(Principal principal, Model model) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
        double subtotal = carritoService.calcularSubtotal(usuario.getId());
        double total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);

        return "carito/carrito";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId, @RequestParam int cantidad, Principal principal, RedirectAttributes redirectAttributes) {
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
    @ResponseBody // Importante para devolver JSON
    public ResponseEntity<?> actualizarCantidadItem(@RequestParam Long itemId,
                                                    @RequestParam int cantidad,
                                                    Principal principal) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.actualizarCantidadItem(usuario.getId(), itemId, cantidad);
            // Devolver el carrito actualizado para que el frontend pueda renderizarlo
            List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
            double subtotal = carritoService.calcularSubtotal(usuario.getId());
            double total = carritoService.calcularTotal(usuario.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cantidad actualizada.",
                    "carritoItems", carritoItems,
                    "subtotal", subtotal,
                    "total", total
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "No tienes permiso para modificar este item."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error interno al actualizar la cantidad."));
        }
    }

    @GetMapping("/eliminar/{itemId}")
    @ResponseBody // Importante para devolver JSON
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long itemId, Principal principal) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
        try {
            carritoService.eliminarProducto(usuario.getId(), itemId);
            // Devolver el carrito actualizado para que el frontend pueda renderizarlo
            List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuario.getId());
            double subtotal = carritoService.calcularSubtotal(usuario.getId());
            double total = carritoService.calcularTotal(usuario.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producto eliminado correctamente.",
                    "carritoItems", carritoItems,
                    "subtotal", subtotal,
                    "total", total
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "No tienes permiso para eliminar este item."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error interno al eliminar el producto."));
        }
    }

    @GetMapping("/checkout")
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
    public String realizarPago(@RequestParam Long metodoPagoId,
                               @RequestParam String calle,
                               @RequestParam String distrito,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioFromPrincipal(principal);
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