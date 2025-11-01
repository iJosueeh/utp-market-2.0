package com.utpmarket.utp_market.models.entity.product;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_detalles")
public class ProductoDetalleView {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;

    @Column(name = "is_destacado")
    private Boolean isDestacado;

    @Column(name = "fecha_creacion")
    private java.sql.Timestamp fechaCreacion;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "estado_id")
    private Long estadoId;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "review_promedio")
    private Double reviewPromedio;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    public ProductoDetalleView() {}

    // Getters
    public Long getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public Boolean getIsDestacado() { return isDestacado; }
    public java.sql.Timestamp getFechaCreacion() { return fechaCreacion; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getEstadoId() { return estadoId; }
    public Long getVendedorId() { return vendedorId; }
    public String getImagenUrl() { return imagenUrl; }
    public Double getReviewPromedio() { return reviewPromedio; }
    public Integer getTotalReviews() { return totalReviews; }

    // No setters → entidad de solo lectura
}
