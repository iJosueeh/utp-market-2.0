package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.ProductoDetalleView;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.repository.ProductoDetalleViewRepository;
import com.utpmarket.utp_market.repository.ReviewsRepository;
import com.utpmarket.utp_market.services.ProductoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.utpmarket.utp_market.services.ProductoService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Controller
@RequestMapping("/producto")
public class ProductoDetalleController {

    @Autowired
    private ProductoDetalleViewRepository productoDetalleViewRepository;

    @Autowired
    private ReviewsRepository reviewRepository;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        // Buscar el producto principal
        Producto producto = productoService.findById(id);

        // Detalle del producto (vista combinada)
        ProductoDetalleView detalle = productoDetalleViewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Productos relacionados
        List<Producto> relacionados = productoService.getProductosRelacionados(id);

        // Reseñas del producto
        List<Reviews> reseñas = reviewRepository.findByProductoId(id);

        // Asegurar que la lista no sea nula
        if (relacionados == null) {
            relacionados = List.of();
        }

        // Agregar atributos al modelo
        model.addAttribute("detalle", detalle);
        model.addAttribute("reseñas", reseñas);
        model.addAttribute("relacionados", relacionados);

        // Retornar vista
        return "producto/detalle";
    }
}
