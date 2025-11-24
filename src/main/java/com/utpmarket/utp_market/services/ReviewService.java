package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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
    public Reviews guardarReview(@NonNull Reviews review) {
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
    public Optional<Reviews> obtenerReviewPorId(@NonNull Long id) {
        return reviewsRepository.findById(id);
    }

    /**
     * Eliminar una review
     */
    @Transactional
    public void eliminarReview(@NonNull Long reviewId, @NonNull Long userId) {
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        if (!review.getUsuario().getId().equals(userId)) {
            throw new SecurityException("No tienes permiso para eliminar esta reseña");
        }
        reviewsRepository.deleteById(reviewId);
    }

    /**
     * Crear una nueva review con validación
     */
    @Transactional
    public Reviews actualizarReview(@NonNull Long reviewId, @NonNull Long usuarioId, @NonNull Integer nuevoPuntaje, String nuevoComentario) {
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        if (!review.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("No tienes permiso para actualizar esta reseña");
        }

        if (nuevoPuntaje < 1 || nuevoPuntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5");
        }

        review.setPuntaje(nuevoPuntaje);
        review.setComentario(nuevoComentario);
        review.setFechaModificacion(LocalDateTime.now());

        return reviewsRepository.save(review);
    }

    @Transactional
    public Reviews crearReview(@NonNull Usuario usuario, @NonNull Producto producto, @NonNull Integer puntaje,
            String comentario) {
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