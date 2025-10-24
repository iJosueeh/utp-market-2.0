package com.utpmarket.utp_market.models.entity.cart;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Timestamp fecha_creacion;

    @OneToMany(mappedBy = "carrito")
    private Set<ItemsCarrito> itemsCarrito;

    public Carrito() {}

    public Carrito(Long id, Usuario usuario, Timestamp fecha_creacion) {
        this.id = id;
        this.usuario = usuario;
        this.fecha_creacion = fecha_creacion;
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

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Set<ItemsCarrito> getItemsCarrito() {
        return itemsCarrito;
    }

    public void setItemsCarrito(Set<ItemsCarrito> itemsCarrito) {
        this.itemsCarrito = itemsCarrito;
    }
}
