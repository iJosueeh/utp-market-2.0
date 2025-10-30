package com.utpmarket.utp_market.models.entity.product;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "etiquetas_productos")
public class EtiquetaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etiqueta_id")
    private Etiqueta etiqueta;

    private Timestamp fecha_asignacion;

    public EtiquetaProducto() {}

    public EtiquetaProducto(Long id, Producto producto, Etiqueta etiqueta, Timestamp fecha_asignacion) {
        this.id = id;
        this.producto = producto;
        this.etiqueta = etiqueta;
        this.fecha_asignacion = fecha_asignacion;
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

    public Etiqueta getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(Etiqueta etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Timestamp getFecha_asignacion() {
        return fecha_asignacion;
    }

    public void setFecha_asignacion(Timestamp fecha_asignacion) {
        this.fecha_asignacion = fecha_asignacion;
    }

}
