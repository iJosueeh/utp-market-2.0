package com.utpmarket.utp_market.models.dto;

public class Contacto {
    private String categoria;
    private String nombre;
    private String correo;
    private String mensaje;

    // Getters and Setters
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "ContactoDTO{" +
               "categoria='" + categoria + "'" +
               ", nombre='" + nombre + "'" +
               ", correo='" + correo + "'" +
               ", mensaje='" + mensaje + "'" +
               '}';
    }
}
