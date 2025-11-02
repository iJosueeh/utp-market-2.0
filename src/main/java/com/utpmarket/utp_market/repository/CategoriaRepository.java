package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT new com.utpmarket.utp_market.models.dto.CategoriaDTO(c.id, c.nombre, c.descripcion, c.icono, COUNT(p.id)) " +
           "FROM Categoria c LEFT JOIN c.productos p GROUP BY c.id, c.nombre, c.descripcion, c.icono")
    List<CategoriaDTO> findAllCategoriasWithProductCount();
}
