package com.utpmarket.utp_market.models.entity.product;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String icono;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private Set<Producto> productos;

    public Categoria() {}

    public Categoria(Long id, String nombre, String descripcion, String icono) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
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

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }
}