package com.utpmarket.utp_market.repository.specifications;

import com.utpmarket.utp_market.models.entity.product.Producto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProductoSpecification {

    public static Specification<Producto> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("categoria").get("nombre"), category);
        };
    }

    public static Specification<Producto> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("estado").get("nombre"), status);
        };
    }
}
