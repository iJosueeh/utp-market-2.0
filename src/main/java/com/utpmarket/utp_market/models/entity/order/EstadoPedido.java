package com.utpmarket.utp_market.models.entity.order;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "estados_pedido")
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @OneToMany(mappedBy = "estado")
    private Set<Pedido> pedidos;

    @OneToMany(mappedBy = "estadoAnterior")
    private Set<HistorialEstadoPedido> historialEstadoPedidosAnterior;

    @OneToMany(mappedBy = "estadoNuevo")
    private Set<HistorialEstadoPedido> historialEstadoPedidosNuevo;

    public EstadoPedido() {}

    public EstadoPedido(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Set<HistorialEstadoPedido> getHistorialEstadoPedidosAnterior() {
        return historialEstadoPedidosAnterior;
    }

    public void setHistorialEstadoPedidosAnterior(Set<HistorialEstadoPedido> historialEstadoPedidosAnterior) {
        this.historialEstadoPedidosAnterior = historialEstadoPedidosAnterior;
    }

    public Set<HistorialEstadoPedido> getHistorialEstadoPedidosNuevo() {
        return historialEstadoPedidosNuevo;
    }

    public void setHistorialEstadoPedidosNuevo(Set<HistorialEstadoPedido> historialEstadoPedidosNuevo) {
        this.historialEstadoPedidosNuevo = historialEstadoPedidosNuevo;
    }
}
