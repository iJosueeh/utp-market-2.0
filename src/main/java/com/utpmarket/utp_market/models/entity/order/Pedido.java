package com.utpmarket.utp_market.models.entity.order;

import com.utpmarket.utp_market.models.embeddable.Direccion;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    private Timestamp fecha_pedido;

    private String numero_pedido;

    @Embedded
    private Direccion direccion;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoPedido estado;

    private Double total;

    @OneToMany(mappedBy = "pedido")
    private Set<ItemPedido> itemsPedido;

    @OneToMany(mappedBy = "pedido")
    private Set<HistorialEstadoPedido> historialEstadoPedidos;

    public Pedido() {}

    public Pedido(Long id, Usuario usuario, MetodoPago metodoPago, Timestamp fecha_pedido, String numero_pedido, Direccion direccion, EstadoPedido estado, Double total) {
        this.id = id;
        this.usuario = usuario;
        this.metodoPago = metodoPago;
        this.fecha_pedido = fecha_pedido;
        this.numero_pedido = numero_pedido;
        this.direccion = direccion;
        this.estado = estado;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Timestamp getFecha_pedido() {
        return fecha_pedido;
    }

    public void setFecha_pedido(Timestamp fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public String getNumero_pedido() {
        return numero_pedido;
    }

    public void setNumero_pedido(String numero_pedido) {
        this.numero_pedido = numero_pedido;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Set<ItemPedido> getItemsPedido() {
        return itemsPedido;
    }

    public void setItemsPedido(Set<ItemPedido> itemsPedido) {
        this.itemsPedido = itemsPedido;
    }

    public Set<HistorialEstadoPedido> getHistorialEstadoPedidos() {
        return historialEstadoPedidos;
    }

    public void setHistorialEstadoPedidos(Set<HistorialEstadoPedido> historialEstadoPedidos) {
        this.historialEstadoPedidos = historialEstadoPedidos;
    }
}