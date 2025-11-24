package com.utpmarket.utp_market.models.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.Set;

import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.product.RespuestaReview;
import com.utpmarket.utp_market.models.entity.cart.Carrito;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "password", "pedidos", "productos", "reviews", "favoritos", "notificaciones", "respuestasReviews",
        "carrito", "citas" })
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    private Boolean activo;

    private Timestamp fecha_registro;

    @PrePersist
    protected void onCreate() {
        fecha_registro = new Timestamp(System.currentTimeMillis());
        activo = true;
    }

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EstudianteDetalles estudianteDetalles;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;

    @OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY)
    private Set<Producto> productos;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Reviews> reviews;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Favorito> favoritos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Notificacion> notificaciones;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<RespuestaReview> respuestasReviews;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Carrito carrito;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Cita> citas;

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}