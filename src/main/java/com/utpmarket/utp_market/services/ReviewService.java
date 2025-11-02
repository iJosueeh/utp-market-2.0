// src/main/java/com/utpmarket/utp_market/models/services/ReviewService.java
package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    /**
     * Obtener todas las reviews de un producto
     */
    @Transactional(readOnly = true)
    public List<Reviews> obtenerReviewsPorProducto(Long productoId) {
        return reviewsRepository.findByProductoId(productoId);
    }

    /**
     * Obtener todas las reviews de un usuario (para su perfil)
     */
    @Transactional(readOnly = true)
    public List<Reviews> obtenerReviewsPorUsuario(Long usuarioId) {
        return reviewsRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Guardar una nueva review
     */
    @Transactional
    public Reviews guardarReview(Reviews review) {
        return reviewsRepository.save(review);
    }

    /**
     * Verificar si un usuario ya dejó review en un producto
     */
    @Transactional(readOnly = true)
    public boolean usuarioYaHizoReview(Long usuarioId, Long productoId) {
        return reviewsRepository.existsByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    /**
     * Obtener el promedio de puntaje de un producto
     */
    @Transactional(readOnly = true)
    public Double obtenerPromedioPuntaje(Long productoId) {
        Double promedio = reviewsRepository.getPromedioPuntajePorProducto(productoId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }

    /**
     * Obtener la cantidad de reviews de un producto
     */
    @Transactional(readOnly = true)
    public Long contarReviewsPorProducto(Long productoId) {
        return reviewsRepository.countByProductoId(productoId);
    }

    /**
     * Obtener una review por ID
     */
    @Transactional(readOnly = true)
    public Optional<Reviews> obtenerReviewPorId(Long id) {
        return reviewsRepository.findById(id);
    }

    /**
     * Eliminar una review
     */
    @Transactional
    public void eliminarReview(Long id) {
        reviewsRepository.deleteById(id);
    }

    /**
     * Crear una nueva review con validación
     */
    @Transactional
    public Reviews crearReview(Usuario usuario, Producto producto, Integer puntaje, String comentario) {
        // Verificar que no exista una review previa
        if (usuarioYaHizoReview(usuario.getId(), producto.getId())) {
            throw new IllegalStateException("Ya has dejado una reseña para este producto");
        }

        // Validar puntaje
        if (puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5");
        }

        // Crear y guardar la review
        Reviews review = new Reviews();
        review.setUsuario(usuario);
        review.setProducto(producto);
        review.setPuntaje(puntaje);
        review.setComentario(comentario);
        review.setFechaCreacion(LocalDateTime.now());

        return reviewsRepository.save(review);
    }
}
