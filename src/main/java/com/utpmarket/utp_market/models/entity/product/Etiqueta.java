package com.utpmarket.utp_market.models.entity.product;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "etiquetas")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String color;

    @OneToMany(mappedBy = "etiqueta", fetch = FetchType.LAZY)
    private Set<EtiquetaProducto> etiquetaProductos;

    public Etiqueta() {}

    public Etiqueta(Long id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<EtiquetaProducto> getEtiquetaProductos() {
        return etiquetaProductos;
    }

    public void setEtiquetaProductos(Set<EtiquetaProducto> etiquetaProductos) {
        this.etiquetaProductos = etiquetaProductos;
    }
}
