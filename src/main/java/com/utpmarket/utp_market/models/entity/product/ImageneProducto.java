package com.utpmarket.utp_market.models.entity.product;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenes_productos")
public class ImageneProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private boolean isPrincipal;

    private String url;

    public ImageneProducto() {}

    public ImageneProducto(Long id, Producto producto, boolean isPrincipal, String url) {
        this.id = id;
        this.producto = producto;
        this.isPrincipal = isPrincipal;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public boolean isPrincipal() {
        return isPrincipal;
    }

    public void setPrincipal(boolean principal) {
        isPrincipal = principal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
