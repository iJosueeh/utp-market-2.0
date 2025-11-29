package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.ProductoAdminDTO;
import com.utpmarket.utp_market.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String viewProductos(Model model) {
        model.addAttribute("activePage", "productos");
        return "admin/productos";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<ProductoAdminDTO>> getProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoAdminDTO> productos = productoService.obtenerProductosPaginadosYFiltrados(pageable, categoria, estado);
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/edit/{id}")
    public String viewEditProducto(@PathVariable Long id, Model model) {
        // This is a placeholder, in a real application you would fetch the product
        // and pass it to the view.
        model.addAttribute("productId", id);
        model.addAttribute("activePage", "productos");
        return "admin/productos_edit";
    }
}
