package com.utpmarket.utp_market.models.entity.cart;

import com.utpmarket.utp_market.models.entity.product.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "items_carritos")
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

    public ItemsCarrito() {}

    public ItemsCarrito(Long id, Carrito carrito, Producto producto, Integer cantidad, Double precio_unitario) {
        this.id = id;
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(Double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

}
