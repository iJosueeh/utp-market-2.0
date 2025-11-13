package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.dto.VendedorDTO;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.estudianteDetalles LEFT JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmail (String email);

    List<Usuario> findByRol_Nombre(String nombre);

    @Query("SELECT new com.utpmarket.utp_market.models.dto.VendedorDTO(u.id, u.nombre, COUNT(p.id)) " +
           "FROM Usuario u LEFT JOIN u.productos p WHERE u.rol.nombre = 'vendedor' GROUP BY u.id, u.nombre")
    List<VendedorDTO> findAllVendedoresWithProductCount();
}