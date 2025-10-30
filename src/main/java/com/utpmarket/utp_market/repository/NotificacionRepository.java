package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.user.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
