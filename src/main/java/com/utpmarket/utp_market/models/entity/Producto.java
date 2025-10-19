package com.utpmarket.utp_market.models.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    private String categoria;
    @Column(length = 2000)
    private String descripcion;
    @Column(nullable = false)
    private BigDecimal precio;
    private BigDecimal precioAnterior;
    private Integer descuento;
    @Column(nullable = false)
    private Integer stock;
    private String imagenUrl;
    @Column(length = 1000)
    private String imagenesAdicionales;
    private Boolean nuevo = false;
    private Boolean destacado = false;
    private Boolean activo = true;
    private Double rating = 0.0;
    private Integer numReviews = 0;

    public Producto() {}

    public Producto(String nombre, String categoria, String descripcion, BigDecimal precio, BigDecimal precioAnterior, Integer descuento, Integer stock, String imagenUrl, String imagenesAdicionales, Boolean nuevo, Boolean destacado, Boolean activo, Double rating, Integer numReviews) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.precioAnterior = precioAnterior;
        this.descuento = descuento;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.imagenesAdicionales = imagenesAdicionales;
        this.nuevo = nuevo;
        this.destacado = destacado;
        this.activo = activo;
        this.rating = rating;
        this.numReviews = numReviews;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(BigDecimal precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getImagenesAdicionales() {
        return imagenesAdicionales;
    }

    public void setImagenesAdicionales(String imagenesAdicionales) {
        this.imagenesAdicionales = imagenesAdicionales;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
}