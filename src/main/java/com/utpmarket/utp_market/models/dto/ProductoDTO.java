package com.utpmarket.utp_market.models.dto;

import java.sql.Timestamp;

public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String estadoNombre;
    private Integer stock;
    private Long categoriaId;
    private String categoriaNombre;
    private Long vendedorId;
    private String vendedorNombre;
    private Boolean isDestacado;
    private Timestamp fechaCreacion;
    private String imagenUrlPrincipal;
    private Double rating;
    private Integer numReviews;
    private Double descuento;
    private Boolean isNuevo;
    private Double precioAnterior;

    public ProductoDTO(Long id, String nombre, String descripcion, Double precio, String estadoNombre, Integer stock,
                       Long categoriaId, String categoriaNombre, Long vendedorId, String vendedorNombre,
                       Boolean isDestacado, Timestamp fechaCreacion, String imagenUrlPrincipal, Double rating,
                       Integer numReviews, Double descuento, Boolean isNuevo, Double precioAnterior) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estadoNombre = estadoNombre;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.vendedorId = vendedorId;
        this.vendedorNombre = vendedorNombre;
        this.isDestacado = isDestacado;
        this.fechaCreacion = fechaCreacion;
        this.imagenUrlPrincipal = imagenUrlPrincipal;
        this.rating = rating;
        this.numReviews = numReviews;
        this.descuento = descuento;
        this.isNuevo = isNuevo;
        this.precioAnterior = precioAnterior;
    }

    // Getters y Setters
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

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public Boolean getIsDestacado() {
        return isDestacado;
    }

    public void setIsDestacado(Boolean isDestacado) {
        this.isDestacado = isDestacado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagenUrlPrincipal() {
        return imagenUrlPrincipal;
    }

    public void setImagenUrlPrincipal(String imagenUrlPrincipal) {
        this.imagenUrlPrincipal = imagenUrlPrincipal;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Integer numReviews) {
        this.numReviews = numReviews;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Boolean getIsNuevo() {
        return isNuevo;
    }

    public void setIsNuevo(Boolean isNuevo) {
        this.isNuevo = isNuevo;
    }

    public Double getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(Double precioAnterior) {
        this.precioAnterior = precioAnterior;
    }
}