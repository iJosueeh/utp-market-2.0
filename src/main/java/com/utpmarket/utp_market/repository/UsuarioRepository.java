package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.estudianteDetalles WHERE u.email = :email")
    Optional<Usuario> findByEmail (String email);
}