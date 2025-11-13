package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.dto.VendedorDTO;
import com.utpmarket.utp_market.services.CategoriaService;
import com.utpmarket.utp_market.services.ProductoService;
import com.utpmarket.utp_market.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class CategoriaPageController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/categoria")
    public String showCategoriaPage(
            @RequestParam(value = "inStockOnly", required = false) Boolean inStockOnly,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "categoryId", required = false) List<Long> categoryIds,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "minRating", required = false) Integer minRating,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            Model model,
            Principal principal) {

        List<CategoriaDTO> categorias = categoriaService.findAllCategoriasWithProductCount();
        List<VendedorDTO> vendedores = usuarioService.findAllVendedoresWithProductCount();

        List<ProductoDTO> productos = productoService.findFilteredProductosDTO(
                inStockOnly,
                minPrice,
                maxPrice,
                categoryIds,
                sellerId,
                searchTerm,
                minRating,
                sortBy
        );

        model.addAttribute("categorias", categorias);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("vendedores", vendedores);

        // Add current filter values to the model to maintain state in the frontend
        model.addAttribute("currentInStockOnly", inStockOnly);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentCategoryIds", categoryIds);
        model.addAttribute("currentSellerId", sellerId);
        model.addAttribute("currentSearchTerm", searchTerm);
        model.addAttribute("currentMinRating", minRating);
        model.addAttribute("currentSortBy", sortBy != null ? sortBy : "relevancia");

        // Set isLoggedIn based on Spring Security Principal
        model.addAttribute("isLoggedIn", principal != null);

        return "pages/categoria";
    }
}
