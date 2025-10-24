package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.RespuestaReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespuestaReviewRepository extends JpaRepository<RespuestaReview, Long> {
}
