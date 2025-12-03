package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utpmarket.utp_market.models.entity.order.ItemPedido;
import com.utpmarket.utp_market.models.entity.order.MetodoPago;
import com.utpmarket.utp_market.models.entity.order.EstadoPedido;
import com.utpmarket.utp_market.models.entity.order.HistorialEstadoPedido;
import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.dto.CarritoItemDTO;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.repository.MetodoPagoRepository;
import com.utpmarket.utp_market.repository.EstadoPedidoRepository;
import com.utpmarket.utp_market.repository.ItemPedidoRepository;
import com.utpmarket.utp_market.repository.HistorialEstadoPedidoRepository;
import com.utpmarket.utp_market.models.dto.PedidoDTO;
import com.utpmarket.utp_market.models.dto.EstadoPedidoDTO;
import com.utpmarket.utp_market.models.dto.UsuarioDTO;
import com.utpmarket.utp_market.models.dto.PedidoDetalleDTO;
import com.utpmarket.utp_market.models.dto.ItemPedidoDTO;
import com.utpmarket.utp_market.models.dto.ProductoBasicoDTO;
import com.utpmarket.utp_market.models.dto.HistorialEstadoPedidoDTO;
import com.utpmarket.utp_market.models.dto.DireccionDTO;
import com.utpmarket.utp_market.models.dto.MetodoPagoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import com.utpmarket.utp_market.repository.UsuarioRepository;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors; // Added missing import

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

    @Autowired
    private HistorialEstadoPedidoRepository historialEstadoPedidoRepository;

    // Obtiene el historial de pedidos de un usuario
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);
    }

    // Obtiene un pedido por su ID
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(@NonNull Long id) {
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
    public Pedido guardarPedido(@NonNull Pedido pedido) {
        Objects.requireNonNull(pedido, "El pedido no puede ser nulo");
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido crearPedido(@NonNull Long usuarioId, @NonNull Long metodoPagoId, @NonNull Direccion direccionEnvio,
            String transactionId) {
        Objects.requireNonNull(usuarioId, "El ID de usuario no puede ser nulo");
        Objects.requireNonNull(metodoPagoId, "El ID de método de pago no puede ser nulo");
        Objects.requireNonNull(direccionEnvio, "La dirección de envío no puede ser nula");

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
            Long productoId = Objects.requireNonNull(item.getProducto().getId(),
                    "El ID del producto no puede ser nulo");
            Producto producto = productoService.findById(productoId);
            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            productoService.reducirStock(productoId, item.getCantidad());
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMetodoPago(metodoPago);
        pedido.setFecha_pedido(Timestamp.valueOf(LocalDateTime.now()));
        pedido.setNumero_pedido(generarNumeroPedido());
        pedido.setDireccion(direccionEnvio);
        pedido.setTransactionId(transactionId);

        Optional<EstadoPedido> estadoInicialOptional = estadoPedidoRepository.findByNombre("Pendiente");
        if (estadoInicialOptional.isEmpty()) {
            throw new IllegalStateException("Estado 'Pendiente' no encontrado.");
        }
        EstadoPedido estadoInicial = estadoInicialOptional.get();
        pedido.setEstado(estadoInicial);

        double totalPedido = carritoService.calcularTotal(usuarioId);
        pedido.setTotal(totalPedido);

        // Guardar el pedido primero para tener el ID
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        Set<ItemPedido> itemsPedido = new HashSet<>();
        for (CarritoItemDTO itemDTO : carritoItems) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedidoGuardado);

            Long productoId = Objects.requireNonNull(itemDTO.getProducto().getId(),
                    "El ID del producto no puede ser nulo");
            itemPedido.setProducto(productoService.findById(productoId));

            itemPedido.setCantidad(itemDTO.getCantidad());
            itemPedido.setPrecioUnitario(itemDTO.getProducto().getPrecio());
            itemPedido.setSubtotal(itemDTO.getSubtotal());
            itemsPedido.add(itemPedido);
        }
        itemPedidoRepository.saveAll(itemsPedido);
        pedidoGuardado.setItemsPedido(itemsPedido);

        carritoService.limpiarCarrito(usuarioId);

        return pedidoGuardado;
    }

    // Obtener todos los pedidos paginados y filtrados
    @Transactional(readOnly = true)
    public Page<PedidoDTO> obtenerPedidosPaginadosYFiltrados(Pageable pageable,
            Long estadoId,
            Timestamp fechaInicio,
            Timestamp fechaFin) {

        System.out.println("PedidoService - obtenerPedidosPaginadosYFiltrados called with:");
        System.out.println("  Pageable: " + pageable);
        System.out.println("  estadoId: " + estadoId);
        System.out.println("  fechaInicio: " + fechaInicio);
        System.out.println("  fechaFin: " + fechaFin);

        Specification<Pedido> spec = (root, query, cb) -> {
            Predicate finalPredicate = cb.conjunction(); // Start with a true predicate

            if (estadoId != null) {
                System.out.println("  Adding estadoId filter: " + estadoId);
                // Join con la entidad EstadoPedido y luego filtrar por su ID
                finalPredicate = cb.and(finalPredicate, cb.equal(root.get("estado").get("id"), estadoId));
            }

            if (fechaInicio != null) {
                System.out.println("  Adding fechaInicio filter: " + fechaInicio);
                finalPredicate = cb.and(finalPredicate, cb.greaterThanOrEqualTo(root.get("fecha_pedido"), fechaInicio));
            }

            if (fechaFin != null) {
                System.out.println("  Adding fechaFin filter: " + fechaFin);
                finalPredicate = cb.and(finalPredicate, cb.lessThanOrEqualTo(root.get("fecha_pedido"), fechaFin));
            }

            return finalPredicate;
        };

        Page<Pedido> pedidosPage = pedidoRepository.findAll(spec, pageable);

        System.out.println("  Pedidos found (total elements): " + pedidosPage.getTotalElements());
        System.out.println("  Pedidos found (current page elements): " + pedidosPage.getNumberOfElements());

        return pedidosPage.map(pedido -> {
            System.out.println("  Mapping Pedido ID: " + pedido.getId());
            UsuarioDTO usuarioDTO;
            if (pedido.getUsuario() != null) {
                System.out.println("    Usuario object exists for Pedido ID " + pedido.getId() + ": ID=" + pedido.getUsuario().getId() + ", NombreCompleto=" + pedido.getUsuario().getNombreCompleto());
                usuarioDTO = new UsuarioDTO(pedido.getUsuario().getId(), pedido.getUsuario().getNombreCompleto());
            } else {
                System.out.println("    Usuario object is NULL for Pedido ID: " + pedido.getId());
                usuarioDTO = new UsuarioDTO(null, "N/A"); // Provide a default DTO for null user
            }
            EstadoPedidoDTO estadoDTO = new EstadoPedidoDTO(pedido.getEstado().getId(), pedido.getEstado().getNombre());
            return new PedidoDTO(
                pedido.getId(),
                pedido.getNumero_pedido(),
                pedido.getFecha_pedido(),
                pedido.getTotal(),
                estadoDTO,
                usuarioDTO
            );
        });
    }

    // Obtener un pedido detallado por su ID
    @Transactional(readOnly = true)
    public PedidoDetalleDTO obtenerPedidoDetallePorId(@NonNull Long id) {
        System.out.println("PedidoService - obtenerPedidoDetallePorId called with ID: " + id);
        Pedido pedido = pedidoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

        System.out.println("  Found Pedido: " + pedido.getNumero_pedido());
        System.out.println("  Usuario: " + (pedido.getUsuario() != null ? pedido.getUsuario().getNombreCompleto() : "N/A"));
        System.out.println("  Estado: " + (pedido.getEstado() != null ? pedido.getEstado().getNombre() : "N/A"));
        System.out.println("  Metodo Pago: " + (pedido.getMetodoPago() != null ? pedido.getMetodoPago().getNombre() : "N/A"));
        System.out.println("  Items Pedido count: " + (pedido.getItemsPedido() != null ? pedido.getItemsPedido().size() : 0));
        System.out.println("  Historial Estado Pedido count: " + (pedido.getHistorialEstadoPedidos() != null ? pedido.getHistorialEstadoPedidos().size() : 0));


        // Map Pedido to PedidoDetalleDTO
        UsuarioDTO usuarioDTO = null;
        if (pedido.getUsuario() != null) {
            usuarioDTO = new UsuarioDTO(pedido.getUsuario().getId(), pedido.getUsuario().getNombreCompleto());
        }

        EstadoPedidoDTO estadoDTO = null;
        if (pedido.getEstado() != null) {
            estadoDTO = new EstadoPedidoDTO(pedido.getEstado().getId(), pedido.getEstado().getNombre());
        }

        MetodoPagoDTO metodoPagoDTO = null;
        if (pedido.getMetodoPago() != null) {
            metodoPagoDTO = new MetodoPagoDTO(
                pedido.getMetodoPago().getId(),
                pedido.getMetodoPago().getNombre(),
                pedido.getMetodoPago().getDescripcion()
            );
        }

        DireccionDTO direccionDTO = null;
        if (pedido.getDireccion() != null) {
            direccionDTO = new DireccionDTO(
                pedido.getDireccion().getCalle(),
                pedido.getDireccion().getDistrito()
            );
        }

        List<ItemPedidoDTO> itemsPedidoDTO = pedido.getItemsPedido().stream()
                .map(item -> new ItemPedidoDTO(
                    item.getId(),
                    new ProductoBasicoDTO(item.getProducto().getId(), item.getProducto().getNombre(), item.getProducto().getPrecio()),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getSubtotal()
                ))
                .collect(Collectors.toList());

        List<HistorialEstadoPedidoDTO> historialEstadoPedidosDTO = pedido.getHistorialEstadoPedidos().stream()
                .map(historial -> {
                    EstadoPedidoDTO estadoAnteriorDTO = historial.getEstadoAnterior() != null ? new EstadoPedidoDTO(historial.getEstadoAnterior().getId(), historial.getEstadoAnterior().getNombre()) : null;
                    EstadoPedidoDTO estadoNuevoDTO = historial.getEstadoNuevo() != null ? new EstadoPedidoDTO(historial.getEstadoNuevo().getId(), historial.getEstadoNuevo().getNombre()) : null;
                    UsuarioDTO responsableDTO = historial.getUsuarioResponsable() != null ? new UsuarioDTO(historial.getUsuarioResponsable().getId(), historial.getUsuarioResponsable().getNombreCompleto()) : null;
                    return new HistorialEstadoPedidoDTO(
                        historial.getId(),
                        historial.getFecha_cambio(),
                        estadoAnteriorDTO,
                        estadoNuevoDTO,
                        responsableDTO
                    );
                })
                .collect(Collectors.toList());


        return new PedidoDetalleDTO(
            pedido.getId(),
            pedido.getNumero_pedido(),
            pedido.getFecha_pedido(),
            pedido.getTotal(),
            estadoDTO,
            usuarioDTO,
            pedido.getTransactionId(),
            direccionDTO,
            metodoPagoDTO,
            itemsPedidoDTO,
            historialEstadoPedidosDTO
        );
    }

    private String generarNumeroPedido() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        return "PED-" + timestamp;
    }

    // Actualizar el estado de un pedido
    @Transactional
    public PedidoDetalleDTO actualizarEstadoPedido(@NonNull Long pedidoId, @NonNull Long newEstadoId, @NonNull Long usuarioResponsableId) {
        System.out.println("PedidoService - actualizarEstadoPedido called for Pedido ID: " + pedidoId + ", new Estado ID: " + newEstadoId + ", Responsable User ID: " + usuarioResponsableId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

        EstadoPedido oldEstado = pedido.getEstado(); // Record old state

        EstadoPedido newEstado = estadoPedidoRepository.findById(newEstadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado con ID: " + newEstadoId));

        Usuario usuarioResponsable = usuarioRepository.findById(usuarioResponsableId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario responsable no encontrado con ID: " + usuarioResponsableId));

        // Update the order's status
        pedido.setEstado(newEstado);
        pedidoRepository.save(pedido);

        // Create a new history entry
        HistorialEstadoPedido historial = new HistorialEstadoPedido();
        historial.setPedido(pedido);
        historial.setFecha_cambio(Timestamp.valueOf(LocalDateTime.now()));
        historial.setEstadoAnterior(oldEstado);
        historial.setEstadoNuevo(newEstado);
        historial.setUsuarioResponsable(usuarioResponsable);
        historialEstadoPedidoRepository.save(historial);

        // Optionally, refetch with details to ensure all relationships are fresh for the DTO mapping
        // or just use the updated 'pedido' object if relationships are already eager-fetched/proxied.
        // For simplicity, we can call obtenerPedidoDetallePorId to get a fully mapped DTO.
        return obtenerPedidoDetallePorId(pedidoId);
    }
}