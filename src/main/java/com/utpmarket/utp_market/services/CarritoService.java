package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.entity.cart.Carrito;
import com.utpmarket.utp_market.models.entity.cart.ItemsCarrito;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.CarritoRepository;
import com.utpmarket.utp_market.repository.ItemsCarritoRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemsCarritoRepository itemsCarritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    public List<CarritoItemDTO> obtenerItems(@NonNull Long userId) {
        Carrito carrito = getOrCreateCarrito(userId);
        return carrito.getItemsCarrito().stream()
                .map(item -> {
                    ProductoDTO productoDTO = productoService.convertToDto(item.getProducto());
                    return new CarritoItemDTO(item.getId(), productoDTO, item.getCantidad());
                })
                .collect(Collectors.toList());
    }

    public void agregarProducto(@NonNull Long userId, @NonNull Long productoId, int cantidad) {
        Carrito carrito = getOrCreateCarrito(userId);
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Optional<ItemsCarrito> itemExistente = carrito.getItemsCarrito().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemsCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            itemsCarritoRepository.save(item);
        } else {
            ItemsCarrito nuevoItem = new ItemsCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecio_unitario(producto.getPrecio());
            carrito.getItemsCarrito().add(nuevoItem);
            itemsCarritoRepository.save(nuevoItem);
        }
    }

    public void eliminarProducto(@NonNull Long userId, @NonNull Long itemId) {
        Carrito carrito = getOrCreateCarrito(userId);
        itemsCarritoRepository.deleteByIdAndCarrito(itemId, carrito);
    }

    public double calcularSubtotal(@NonNull Long userId) {
        List<CarritoItemDTO> items = obtenerItems(userId);
        return items.stream()
                .mapToDouble(CarritoItemDTO::getSubtotal)
                .sum();
    }

    public double calcularTotal(@NonNull Long userId) {
        double subtotal = calcularSubtotal(userId);
        double envio = (subtotal > 100) ? 0 : 10;
        return subtotal + envio;
    }

    public void limpiarCarrito(@NonNull Long userId) {
        Carrito carrito = getOrCreateCarrito(userId);
        itemsCarritoRepository.deleteAllByCarrito(carrito);
    }

    public void actualizarCantidadItem(@NonNull Long userId, @NonNull Long itemId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarProducto(userId, itemId);
            return;
        }

        Carrito carrito = getOrCreateCarrito(userId);
        ItemsCarrito item = itemsCarritoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado"));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new SecurityException("El item no pertenece al carrito del usuario");
        }

        Producto producto = item.getProducto();
        if (nuevaCantidad > producto.getStock()) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
        }

        item.setCantidad(nuevaCantidad);
        itemsCarritoRepository.save(item);
    }

    private Carrito getOrCreateCarrito(@NonNull Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setFecha_creacion(Timestamp.from(Instant.now()));
                    return carritoRepository.save(nuevoCarrito);
                });
    }
}
