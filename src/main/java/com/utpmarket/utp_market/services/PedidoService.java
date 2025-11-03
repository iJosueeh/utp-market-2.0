package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utpmarket.utp_market.models.entity.order.ItemPedido;
import com.utpmarket.utp_market.models.entity.order.MetodoPago;
import com.utpmarket.utp_market.models.entity.order.EstadoPedido;
import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.repository.MetodoPagoRepository;
import com.utpmarket.utp_market.repository.EstadoPedidoRepository;
import com.utpmarket.utp_market.repository.ItemPedidoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    // Obtiene el historial de pedidos de un usuario
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);
    }

    // Obtiene un pedido por su ID
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    // Obtiene un pedido por su numero de pedido
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorNumero(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    // Obtener todos los pedidos
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    // Guarda y Actualiza
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido crearPedido(Long usuarioId, Long metodoPagoId, Direccion direccionEnvio) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

        List<CarritoItemDTO> carritoItems = carritoService.obtenerItems(usuarioId);
        if (carritoItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío. No se puede crear un pedido.");
        }

        // Verificar stock y reducirlo
        for (CarritoItemDTO item : carritoItems) {
            Producto producto = productoService.findById(item.getProducto().getId());
            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            productoService.reducirStock(producto.getId(), item.getCantidad());
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMetodoPago(metodoPago);
        pedido.setFecha_pedido(Timestamp.valueOf(LocalDateTime.now()));
        pedido.setNumero_pedido(generarNumeroPedido());
        pedido.setDireccion(direccionEnvio);

        // Estado inicial del pedido (por ejemplo, "Pendiente")
        Optional<EstadoPedido> estadoInicialOptional = estadoPedidoRepository.findByNombre("Pendiente");
        if (estadoInicialOptional.isEmpty()) {
            throw new IllegalStateException("Estado 'Pendiente' no encontrado.");
        }
        EstadoPedido estadoInicial = estadoInicialOptional.get();
        pedido.setEstado(estadoInicial);

        double totalPedido = carritoService.calcularTotal(usuarioId);
        pedido.setTotal(totalPedido);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        Set<ItemPedido> itemsPedido = new HashSet<>();
        for (CarritoItemDTO itemDTO : carritoItems) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedidoGuardado);
            itemPedido.setProducto(productoService.findById(itemDTO.getProducto().getId()));
            itemPedido.setCantidad(itemDTO.getCantidad());
            itemPedido.setPrecioUnitario(itemDTO.getProducto().getPrecio());
            itemsPedido.add(itemPedido);
        }
        itemPedidoRepository.saveAll(itemsPedido);
        pedidoGuardado.setItemsPedido(itemsPedido);

        carritoService.limpiarCarrito(usuarioId); // Limpiar carrito después de crear el pedido

        return pedidoGuardado;
    }

    private String generarNumeroPedido() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        return "PED-" + timestamp;
    }
}