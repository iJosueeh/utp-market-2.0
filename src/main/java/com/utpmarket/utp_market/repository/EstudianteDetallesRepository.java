package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteDetallesRepository extends JpaRepository<EstudianteDetalles, Long> {
}
