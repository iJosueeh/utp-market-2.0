package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.ProductoAdminDTO;
import com.utpmarket.utp_market.models.dto.ProductoUpdateDTO;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.services.CategoriaService;
import com.utpmarket.utp_market.services.EstadoProductoService;
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

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private EstadoProductoService estadoProductoService;

    @GetMapping
    public String viewProductos(Model model) {
        model.addAttribute("activePage", "productos");
        model.addAttribute("allCategorias", categoriaService.findAllCategoriasWithProductCount());
        model.addAttribute("allEstados", estadoProductoService.findAll());
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


    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProductoAdminDTO> getProducto(@PathVariable Long id) {
        ProductoAdminDTO producto = productoService.findByIdToUpdate(id);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody ProductoUpdateDTO productoUpdateDTO) {
        Producto updatedProducto = productoService.updateProducto(id, productoUpdateDTO);
        return ResponseEntity.ok(updatedProducto);
    }

    @GetMapping("/fragments/edit-modal")
    public String getEditModalFragment() {
        return "fragments/modal_content_edit :: content";
    }

    @GetMapping("/fragments/delete-modal")
    public String getDeleteModalFragment() {
        return "fragments/modal_content_delete :: content";
    }
}