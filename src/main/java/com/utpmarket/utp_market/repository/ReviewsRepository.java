package com.utpmarket.utp_market.repository;

import com.utpmarket.utp_market.models.entity.product.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
}
