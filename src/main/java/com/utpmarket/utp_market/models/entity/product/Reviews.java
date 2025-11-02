package com.utpmarket.utp_market.models.entity.product;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;
import java.util.Set;
import java.time.LocalDateTime; // Necesario para fechaCreacion

@Entity
@Table(name = "reviews")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Integer puntaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion; // Para registrar cuándo se envió

    @OneToMany(mappedBy = "reviews", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RespuestaReview> respuestas;

    // --- CONSTRUCTORES ---
    public Reviews() {}

    public Reviews(Long id, String comentario, Producto producto, Usuario usuario, Integer puntaje) {
        this.id = id;
        this.comentario = comentario;
        this.producto = producto;
        this.usuario = usuario;
        this.puntaje = puntaje;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public Set<RespuestaReview> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Set<RespuestaReview> respuestas) {
        this.respuestas = respuestas;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}