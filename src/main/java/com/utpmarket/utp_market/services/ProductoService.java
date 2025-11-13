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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (producto.getCategoria() == null) {
            return List.of();
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

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(searchTerm);
        } else {
            productos = obtenerProductosBase(inStockOnly);
        }

        productos = aplicarFiltroCategorias(productos, categoryIds);
        productos = aplicarFiltroVendedor(productos, sellerId);
        productos = aplicarFiltroPrecios(productos, minPrice, maxPrice);
        productos = aplicarFiltroCalificacionMinima(productos, minRating);
        productos = aplicarOrdenacion(productos, sortBy);

        return productos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductoDTO convertToDto(Producto producto) {
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

    private List<Producto> aplicarOrdenacion(List<Producto> productos, String sortBy) {
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
                    break;
            }
        }
        return productos;
    }

    private List<Producto> aplicarFiltroCalificacionMinima(List<Producto> productos, Integer minRating) {
        if (minRating != null) {
            return productos.stream()
                    .filter(p -> {
                        Double averageRating = p.getReviews().stream()
                                .mapToInt(Reviews::getPuntaje)
                                .average()
                                .orElse(0.0);
                        return averageRating >= minRating;
                    })
                    .collect(Collectors.toList());
        }
        return productos;
    }

    private List<Producto> aplicarFiltroPrecios(List<Producto> productos, Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return productos.stream()
                    .filter(p -> p.getPrecio() >= minPrice && p.getPrecio() <= maxPrice)
                    .collect(Collectors.toList());
        } else if (minPrice != null) {
            return productos.stream()
                    .filter(p -> p.getPrecio() >= minPrice)
                    .collect(Collectors.toList());
        } else if (maxPrice != null) {
            return productos.stream()
                    .filter(p -> p.getPrecio() <= maxPrice)
                    .collect(Collectors.toList());
        }
        return productos;
    }

    private List<Producto> aplicarFiltroVendedor(List<Producto> productos, Long sellerId) {
        if (sellerId != null) {
            Optional<Usuario> vendedor = usuarioRepository.findById(sellerId);
            if (vendedor.isPresent()) {
                return productos.stream()
                        .filter(p -> p.getVendedor() != null && p.getVendedor().equals(vendedor.get()))
                        .collect(Collectors.toList());
            }
        }
        return productos;
    }

    private List<Producto> aplicarFiltroCategorias(List<Producto> productos, List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return productos.stream()
                    .filter(p -> p.getCategoria() != null && categoryIds.contains(p.getCategoria().getId()))
                    .collect(Collectors.toList());
        }
        return productos;
    }

    private List<Producto> obtenerProductosBase(Boolean enStockSolamente) {
        if (enStockSolamente != null && enStockSolamente) {
            return productoRepository.findByStockGreaterThan(0);
        } else {
            return productoRepository.findAll();
        }
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public void reducirStock(Long productoId, int cantidad) {
        Producto producto = findById(productoId);
        int nuevoStock = producto.getStock() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }
}