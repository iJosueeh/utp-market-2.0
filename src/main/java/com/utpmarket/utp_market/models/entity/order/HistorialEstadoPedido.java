package com.utpmarket.utp_market.models.entity.order;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "historial_estado_pedidos")
public class HistorialEstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable_id")
    private Usuario usuarioResponsable;

    private Timestamp fecha_cambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_anterior_id")
    private EstadoPedido estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_nuevo_id")
    private EstadoPedido estadoNuevo;

    public HistorialEstadoPedido() {}

    public HistorialEstadoPedido(Long id, Pedido pedido, Usuario usuarioResponsable, Timestamp fecha_cambio, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo) {
        this.id = id;
        this.pedido = pedido;
        this.usuarioResponsable = usuarioResponsable;
        this.fecha_cambio = fecha_cambio;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public Timestamp getFecha_cambio() {
        return fecha_cambio;
    }

    public void setFecha_cambio(Timestamp fecha_cambio) {
        this.fecha_cambio = fecha_cambio;
    }

    public EstadoPedido getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoPedido estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoPedido getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoPedido estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }
}