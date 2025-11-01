package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.ProductoService;
import com.utpmarket.utp_market.services.ReviewService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class ProductoDetalleController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id,
                                     HttpSession session,
                                     Model model,
                                     @RequestParam(required = false) String success,
                                     @RequestParam(required = false) String error) {

        Producto detalle = null;

        try {
            // 1. Intentamos obtener el producto
            detalle = productoService.findById(id);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println("ERROR: Producto con ID " + id + " no encontrado o inválido.");
        }

        // 🚨 CORRECCIÓN CLAVE: La variable 'detalle' debe ser agregada al Model
        // antes de cualquier retorno para que Thymeleaf no falle si es nulo.
        model.addAttribute("detalle", detalle);
        model.addAttribute("success", success);
        model.addAttribute("error", error);


        // 2. Si el producto no existe, la vista (detalle.html) se encargará de mostrar
        // el mensaje de "Producto no encontrado" gracias al 'th:if="${detalle == null}"'
        // y como 'detalle' ya está en el Model, se evita el error de parseo.
        if (detalle == null) {
            return "producto/detalle";
        }

        // --- SI EL PRODUCTO EXISTE, CONTINUAMOS CARGANDO LOS DEMÁS DATOS ---

        // --- Reviews del producto ---
        double reviewPromedio = reviewService.obtenerPromedioPuntaje(id);
        long totalReviews = reviewService.contarReviewsPorProducto(id);

        List<Reviews> reseñas = reviewService.obtenerReviewsPorProducto(id);

        model.addAttribute("reviewPromedio", reviewPromedio);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("reseñas", reseñas);

        // --- Usuario actual ---
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        boolean usuarioYaReseño = false;

        if (usuario != null) {
            usuarioYaReseño = reviewService.usuarioYaHizoReview(usuario.getId(), id);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioYaReseño", usuarioYaReseño);

        // --- Productos relacionados ---
        List<Producto> relacionados = productoService.getProductosRelacionados(id);
        model.addAttribute("relacionados", relacionados);

        return "producto/detalle";
    }
}