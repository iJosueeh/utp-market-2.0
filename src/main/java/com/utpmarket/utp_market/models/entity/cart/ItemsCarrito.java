package com.utpmarket.utp_market.models.entity.cart;

import com.utpmarket.utp_market.models.entity.product.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "items_carritos")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "carrito", "producto" })
@EqualsAndHashCode(of = "id")
public class ItemsCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private Double precio_unitario;
}
