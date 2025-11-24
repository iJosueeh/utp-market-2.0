package com.utpmarket.utp_market.models.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Entity
@Table(name = "estados_pedido")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "pedidos", "historialEstadoPedidosAnterior", "historialEstadoPedidosNuevo" })
@EqualsAndHashCode(of = "id")
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;

    @OneToMany(mappedBy = "estadoAnterior", fetch = FetchType.LAZY)
    private Set<HistorialEstadoPedido> historialEstadoPedidosAnterior;

    @OneToMany(mappedBy = "estadoNuevo", fetch = FetchType.LAZY)
    private Set<HistorialEstadoPedido> historialEstadoPedidosNuevo;
}
