package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.entity.product.ImageneProducto;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.CategoriaRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Producto> getProductosRelacionados(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getCategoria() == null) {
            return List.of(); // 🔹 devuelve lista vacía si no tiene categoría
        }

        Long categoriaId = producto.getCategoria().getId();
        return productoRepository.findRelatedProducts(categoriaId, productoId);
    }

    public List<ProductoDTO> findAllProductosDTO() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> findFilteredProductosDTO(
            Boolean inStockOnly,
            Double minPrice,
            Double maxPrice,
            List<Long> categoryIds,
            Long sellerId,
            String searchTerm,
            Integer minRating,
            String sortBy) {

        List<Producto> productos;

        // Base query: all products or only in-stock products
        if (inStockOnly != null && inStockOnly) {
            productos = productoRepository.findByStockGreaterThan(0);
        } else {
            productos = productoRepository.findAll();
        }

        // Apply filters sequentially
        if (categoryIds != null && !categoryIds.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && categoryIds.contains(p.getCategoria().getId()))
                    .collect(Collectors.toList());
        }

        // Fetch seller entity if ID is provided
        Optional<Usuario> vendedor = sellerId != null ? usuarioRepository.findById(sellerId) : Optional.empty();
        if (vendedor.isPresent()) {
            productos = productos.stream()
                    .filter(p -> p.getVendedor() != null && p.getVendedor().equals(vendedor.get()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null && maxPrice != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() >= minPrice && p.getPrecio() <= maxPrice)
                    .collect(Collectors.toList());
        } else if (minPrice != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() >= minPrice)
                    .collect(Collectors.toList());
        } else if (maxPrice != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String lowerCaseSearchTerm = searchTerm.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(lowerCaseSearchTerm) ||
                                 p.getDescripcion().toLowerCase().contains(lowerCaseSearchTerm))
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            productos = productos.stream()
                    .filter(p -> {
                        Double averageRating = p.getReviews().stream()
                                .mapToInt(Reviews::getPuntaje)
                                .average()
                                .orElse(0.0);
                        return averageRating >= minRating;
                    })
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "precio-asc":
                    productos.sort(Comparator.comparing(Producto::getPrecio));
                    break;
                case "precio-desc":
                    productos.sort(Comparator.comparing(Producto::getPrecio).reversed());
                    break;
                case "nombre-asc":
                    productos.sort(Comparator.comparing(Producto::getNombre));
                    break;
                case "rating-desc":
                    productos.sort(Comparator.comparing(p -> p.getReviews().stream()
                            .mapToInt(Reviews::getPuntaje)
                            .average()
                            .orElse(0.0), Comparator.reverseOrder()));
                    break;
                case "relevancia":
                default:
                    // Default sorting (e.g., by creation date or a relevance score)
                    // For now, let's assume 'relevancia' is just the default order from the DB or no specific sort.
                    // If you have a 'relevance' field, you would sort by that.
                    break;
            }
        }

        return productos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductoDTO convertToDto(Producto producto) {
        String imagenUrlPrincipal = producto.getImagenes().stream()
                .filter(ImageneProducto::isPrincipal)
                .map(ImageneProducto::getUrl)
                .findFirst()
                .orElse(producto.getImagenes().stream()
                        .map(ImageneProducto::getUrl)
                        .findFirst()
                        .orElse("/images/placeholder.png"));

        double rating = 0.0;
        int numReviews = producto.getReviews().size();
        if (numReviews > 0) {
            rating = producto.getReviews().stream()
                    .mapToInt(Reviews::getPuntaje)
                    .average()
                    .orElse(0.0);
        }

        boolean isNuevo = false;
        if (producto.getFecha_creacion() != null) {
            Instant now = Instant.now();
            Instant fechaCreacionInstant = producto.getFecha_creacion().toInstant();
            long daysBetween = Duration.between(fechaCreacionInstant, now).toDays();
            isNuevo = daysBetween <= 30;
        }

        Double descuento = 0.0;
        Double precioAnterior = null;

        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getEstado() != null ? producto.getEstado().getNombre() : "Desconocido",
                producto.getStock(),
                producto.getCategoria() != null ? producto.getCategoria().getId() : null,
                producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Desconocida",
                producto.getVendedor() != null ? producto.getVendedor().getId() : null,
                producto.getVendedor() != null ? producto.getVendedor().getNombre() : "Desconocido",
                producto.getDestacado(),
                producto.getFecha_creacion(),
                imagenUrlPrincipal,
                rating,
                numReviews,
                descuento,
                isNuevo,
                precioAnterior
        );
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }}
