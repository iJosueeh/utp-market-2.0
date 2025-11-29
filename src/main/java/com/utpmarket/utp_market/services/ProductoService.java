package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.ProductoAdminDTO;
import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.dto.ProductoUpdateDTO;
import com.utpmarket.utp_market.models.entity.product.Categoria;
import com.utpmarket.utp_market.models.entity.product.EstadoProducto;
import com.utpmarket.utp_market.models.entity.product.ImageneProducto;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.CategoriaRepository;
import com.utpmarket.utp_market.repository.EstadoProductoRepository;
import com.utpmarket.utp_market.repository.ImageneProductoRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.repository.specifications.ProductoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoProductoRepository estadoProductoRepository;

    @Autowired
    private ImageneProductoRepository imageneProductoRepository;

    public Page<ProductoAdminDTO> obtenerProductosPaginadosYFiltrados(Pageable pageable, String categoria, String estado) {
        Specification<Producto> spec = Specification.where(ProductoSpecification.hasCategory(categoria))
                .and(ProductoSpecification.hasStatus(estado));
        return productoRepository.findAll(spec, pageable).map(this::convertToAdminDto);
    }

    public ProductoAdminDTO findByIdToUpdate(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return convertToAdminDto(producto);
    }

    public Producto updateProducto(Long id, ProductoUpdateDTO productoUpdateDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        producto.setNombre(productoUpdateDTO.getNombre());
        producto.setPrecio(productoUpdateDTO.getPrecio());
        producto.setStock(productoUpdateDTO.getStock());

        // Handle image URL update
        if (productoUpdateDTO.getImagenUrl() != null && !productoUpdateDTO.getImagenUrl().isEmpty()) {
            Optional<ImageneProducto> principalImageOpt = producto.getImagenes().stream()
                    .filter(ImageneProducto::isPrincipal)
                    .findFirst();

            if (principalImageOpt.isPresent()) {
                ImageneProducto principalImage = principalImageOpt.get();
                if (!principalImage.getUrl().equals(productoUpdateDTO.getImagenUrl())) {
                    principalImage.setUrl(productoUpdateDTO.getImagenUrl());
                    imageneProductoRepository.save(principalImage);
                }
            } else {
                // If no principal image exists, create a new one
                ImageneProducto newPrincipalImage = new ImageneProducto();
                newPrincipalImage.setProducto(producto);
                newPrincipalImage.setPrincipal(true);
                newPrincipalImage.setUrl(productoUpdateDTO.getImagenUrl());
                imageneProductoRepository.save(newPrincipalImage);
                producto.getImagenes().add(newPrincipalImage);
            }
        }

        if (productoUpdateDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(productoUpdateDTO.getCategoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        if (productoUpdateDTO.getEstadoId() != null) {
            EstadoProducto estadoProducto = estadoProductoRepository.findById(productoUpdateDTO.getEstadoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado de producto no encontrado"));
            producto.setEstado(estadoProducto);
        }

        return productoRepository.save(producto);
    }

    private ProductoAdminDTO convertToAdminDto(Producto producto) {
        ProductoAdminDTO dto = new ProductoAdminDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : "N/A");
        dto.setEstado(producto.getEstado() != null ? producto.getEstado().getNombre() : "N/A");
        dto.setVendedor(producto.getVendedor() != null ? producto.getVendedor().getNombre() : "N/A");
        dto.setImagenUrl(producto.getImagenPrincipalUrl());
        dto.setCategoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setEstadoId(producto.getEstado() != null ? producto.getEstado().getId() : null);
        return dto;
    }

    public List<Producto> getProductosRelacionados(@NonNull Long productoId) {
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

    public ProductoDTO convertToDto(@NonNull Producto producto) {
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
                precioAnterior);
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

    public Producto findById(@NonNull Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public void reducirStock(@NonNull Long productoId, int cantidad) {
        Producto producto = findById(productoId);
        int nuevoStock = producto.getStock() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }
}
