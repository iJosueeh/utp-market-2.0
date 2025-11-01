// src/main/java/com/utpmarket/utp_market/controllers/ReviewController.java
package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.ReviewService;
import com.utpmarket.utp_market.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Crear una nueva review
     * POST /reviews/crear
     */
    @PostMapping("/crear")
    public String crearReview(
            @RequestParam Long productoId,
            @RequestParam Integer puntaje,
            @RequestParam String comentario,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener usuario de la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para dejar una reseña");
                return "redirect:/login";
            }

            // Buscar el producto
            Producto producto = productoRepository.findById(productoId).orElse(null);

            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos";
            }

            // Crear la review
            reviewService.crearReview(usuario, producto, puntaje, comentario);

            redirectAttributes.addFlashAttribute("success", "¡Reseña enviada exitosamente!");
            return "redirect:/productos/detalle/" + productoId;

        } catch (IllegalStateException e) {
            // Ya existe una review del usuario para este producto
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/detalle/" + productoId;

        } catch (IllegalArgumentException e) {
            // Error de validación (puntaje inválido, etc.)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/detalle/" + productoId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar la reseña. Intenta nuevamente.");
            return "redirect:/productos/detalle/" + productoId;
        }
    }

    /**
     * Eliminar una review (solo el dueño puede eliminarla)
     * POST /reviews/eliminar/{id}
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarReview(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión");
                return "redirect:/login";
            }

            // Obtener la review
            Reviews review = reviewService.obtenerReviewPorId(id);

            if (review == null) {
                redirectAttributes.addFlashAttribute("error", "Reseña no encontrada");
                return "redirect:/perfil";
            }

            // Verificar que el usuario es el dueño de la review
            if (!review.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta reseña");
                return "redirect:/perfil";
            }

            // Eliminar la review
            reviewService.eliminarReview(id);

            redirectAttributes.addFlashAttribute("success", "Reseña eliminada exitosamente");
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