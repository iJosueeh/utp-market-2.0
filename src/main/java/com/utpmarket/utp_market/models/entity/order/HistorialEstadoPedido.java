package com.utpmarket.utp_market.models.entity.order;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@Entity
@Table(name = "historial_estado_pedidos")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "pedido", "usuarioResponsable", "estadoAnterior", "estadoNuevo" })
@EqualsAndHashCode(of = "id")
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
}