package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.ProductoDetalleView;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.ProductoDetalleViewRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.ProductoService;
import com.utpmarket.utp_market.services.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductoDetalleController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductoDetalleViewRepository productoDetalleViewRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id,
            Principal principal,
            Model model,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error) {

        ProductoDetalleView detalle = productoDetalleViewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        model.addAttribute("detalle", detalle);
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        double reviewPromedio = reviewService.obtenerPromedioPuntaje(id);
        long totalReviews = reviewService.contarReviewsPorProducto(id);

        List<Reviews> resenas = reviewService.obtenerReviewsPorProducto(id);

        model.addAttribute("reviewPromedio", reviewPromedio);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("resenas", resenas);

        Usuario usuario = null;
        boolean usuarioYaResenio = false;

        if (principal != null) {
            usuario = usuarioRepository.findByEmail(principal.getName()).orElse(null);

            if (usuario != null) {
                usuarioYaResenio = reviewService.usuarioYaHizoReview(usuario.getId(), id);
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioYaResenio", usuarioYaResenio);

        List<Producto> relacionados = productoService.getProductosRelacionados(id);
        model.addAttribute("relacionados", relacionados);

        return "producto/detalle";
    }
}