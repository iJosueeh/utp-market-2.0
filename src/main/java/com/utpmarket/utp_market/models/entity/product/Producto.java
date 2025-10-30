package com.utpmarket.utp_market.models.entity.product;

import com.utpmarket.utp_market.models.entity.cart.ItemsCarrito;
import com.utpmarket.utp_market.models.entity.order.ItemPedido;
import com.utpmarket.utp_market.models.entity.user.Favorito;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoProducto estado;

    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    private Boolean isDestacado;
    private Timestamp fecha_creacion;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageneProducto> imagenes;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reviews> reviews;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos;

    @OneToMany(mappedBy = "producto")
    private Set<ItemPedido> itemsPedido;

    @OneToMany(mappedBy = "producto")
    private Set<ItemsCarrito> itemsCarrito;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EtiquetaProducto> etiquetaProductos;

    public Producto() {}

    public Producto(Long id, String nombre, String descripcion, Double precio, EstadoProducto estado, Integer stock, Categoria categoria, Usuario vendedor, Boolean isDestacado, Timestamp fecha_creacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.stock = stock;
        this.categoria = categoria;
        this.vendedor = vendedor;
        this.isDestacado = isDestacado;
        this.fecha_creacion = fecha_creacion;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Boolean getDestacado() {
        return isDestacado;
    }

    public void setDestacado(Boolean destacado) {
        isDestacado = destacado;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Set<ImageneProducto> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Set<ImageneProducto> imagenes) {
        this.imagenes = imagenes;
    }

    public Set<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Reviews> reviews) {
        this.reviews = reviews;
    }

    public Set<Favorito> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Set<Favorito> favoritos) {
        this.favoritos = favoritos;
    }

    public Set<ItemPedido> getItemsPedido() {
        return itemsPedido;
    }

    public void setItemsPedido(Set<ItemPedido> itemsPedido) {
        this.itemsPedido = itemsPedido;
    }

    public Set<ItemsCarrito> getItemsCarrito() {
        return itemsCarrito;
    }

    public void setItemsCarrito(Set<ItemsCarrito> itemsCarrito) {
        this.itemsCarrito = itemsCarrito;
    }

    public Set<EtiquetaProducto> getEtiquetaProductos() {
        return etiquetaProductos;
    }

    public void setEtiquetaProductos(Set<EtiquetaProducto> etiquetaProductos) {
        this.etiquetaProductos = etiquetaProductos;
    }
}
