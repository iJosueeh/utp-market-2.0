package com.utpmarket.utp_market.models.entity.user;

import jakarta.persistence.*;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private boolean isLeida;

    private String tipo;
    private String titulo;
    private String mensaje;

    public Notificacion() {}

    public Notificacion(Long id, Usuario usuario, boolean isLeida, String tipo, String titulo, String mensaje) {
        this.id = id;
        this.usuario = usuario;
        this.isLeida = isLeida;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isLeida() {
        return isLeida;
    }

    public void setLeida(boolean leida) {
        isLeida = leida;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
