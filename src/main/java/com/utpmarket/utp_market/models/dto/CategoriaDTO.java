package com.utpmarket.utp_market.models.dto;

public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Long cantidadProductos; // Para mostrar en el filtro, por ejemplo

    public CategoriaDTO(Long id, String nombre, String descripcion, String icono, Long cantidadProductos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.cantidadProductos = cantidadProductos;
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Long getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Long cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }
}
