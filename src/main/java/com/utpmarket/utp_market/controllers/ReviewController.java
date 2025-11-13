package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.ReviewService;
import com.utpmarket.utp_market.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crear una nueva review
     * POST /reviews/crear
     */
    @PostMapping("/crear")
    @PreAuthorize("isAuthenticated()")
    public String crearReview(
            @RequestParam Long productoId,
            @RequestParam Integer puntaje,
            @RequestParam String comentario,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            reviewService.crearReview(usuario, producto, puntaje, comentario);

            redirectAttributes.addFlashAttribute("success", "¡Reseña enviada exitosamente!");
            return "redirect:/producto/" + productoId;

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/producto/" + productoId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar la reseña. Intenta nuevamente.");
            return "redirect:/producto/" + productoId;
        }
    }

    /**
     * Actualizar una review (solo el dueño puede actualizarla)
     * POST /reviews/actualizar
     */
    @PostMapping("/actualizar")
    @PreAuthorize("isAuthenticated()")
    public String actualizarReview(
            @RequestParam Long reviewId,
            @RequestParam Integer puntaje,
            @RequestParam String comentario,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

            reviewService.actualizarReview(reviewId, usuario.getId(), puntaje, comentario);
            redirectAttributes.addFlashAttribute("success", "Reseña actualizada exitosamente!");
            
            // Redirigir de vuelta a la página del producto
            return reviewService.obtenerReviewPorId(reviewId)
                    .map(review -> "redirect:/producto/" + review.getProducto().getId())
                    .orElse("redirect:/perfil");

        } catch (SecurityException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la reseña. Intenta nuevamente.");
            return "redirect:/perfil";
        }
    }

    /**
     * Eliminar una review (solo el dueño puede eliminarla)
     * POST /reviews/eliminar/{id}
     */
    @PostMapping("/eliminar/{id}")
    @PreAuthorize("isAuthenticated()")
    public String eliminarReview(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

            reviewService.eliminarReview(id, usuario.getId());

            redirectAttributes.addFlashAttribute("success", "Reseña eliminada exitosamente");
            return "redirect:/perfil";

        } catch (SecurityException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la reseña");
            return "redirect:/perfil";
        }
    }

    /**
     * Ver todas las reviews de un producto (JSON para AJAX - opcional)
     * GET /reviews/producto/{productoId}
     */
    @GetMapping("/producto/{productoId}")
    @ResponseBody
    public List<Reviews> obtenerReviewsProducto(@PathVariable Long productoId) {
        return reviewService.obtenerReviewsPorProducto(productoId);
    }
}