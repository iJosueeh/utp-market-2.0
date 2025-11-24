package com.utpmarket.utp_market.models.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import com.utpmarket.utp_market.models.entity.product.Producto;

@Entity
@Table(name = "favoritos")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "usuario", "producto" })
@EqualsAndHashCode(of = "id")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
}
