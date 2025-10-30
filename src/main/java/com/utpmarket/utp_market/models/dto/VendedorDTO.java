package com.utpmarket.utp_market.models.dto;

public class VendedorDTO {
    private Long id;
    private String nombre;
    private Long cantidadProductos;

    public VendedorDTO(Long id, String nombre, Long cantidadProductos) {
        this.id = id;
        this.nombre = nombre;
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

    public Long getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Long cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }
}
