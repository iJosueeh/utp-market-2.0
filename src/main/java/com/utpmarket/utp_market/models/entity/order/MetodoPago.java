package com.utpmarket.utp_market.models.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Entity
@Table(name = "metodos_pago")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "pedidos")
@EqualsAndHashCode(of = "id")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "metodoPago", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;
}
