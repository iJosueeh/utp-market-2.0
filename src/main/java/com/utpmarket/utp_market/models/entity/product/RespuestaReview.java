package com.utpmarket.utp_market.models.entity.product;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "respuestas_reviews")
public class RespuestaReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviews_id")
    private Reviews reviews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String comentario;

    public RespuestaReview() {}

    public RespuestaReview(Long id, Reviews reviews, Usuario usuario, String comentario) {
        this.id = id;
        this.reviews = reviews;
        this.usuario = usuario;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
