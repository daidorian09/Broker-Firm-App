package com.brokage.firm.infrastructure.persistence.jpa.spec;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class OrderSpecification {

    public static Specification<OrderEntity> byFilter(final OrderFilter filter) {
        return new GenericSpecificationBuilder<OrderEntity>()
                .withCondition(Objects.nonNull(filter.customerId()),
                        (root, cb) -> cb.equal(root.get("customerId"), filter.customerId()))

                .withCondition(Objects.nonNull(filter.from()),
                        (root, cb) -> cb.greaterThanOrEqualTo(root.get("createDate"), filter.from().atStartOfDay()))

                .withCondition(Objects.nonNull(filter.to()),
                        (root, cb) -> cb.lessThanOrEqualTo(root.get("createDate"), filter.to().plusDays(1).atStartOfDay()))

                .withCondition(Objects.nonNull(filter.orderSide()),
                        (root, cb) -> cb.equal(root.get("orderSide"), filter.orderSide()))

                .withCondition(Objects.nonNull(filter.status()),
                        (root, cb) -> cb.equal(root.get("status"), filter.status()))

                .withCondition(Objects.nonNull(filter.minSize()),
                        (root, cb) -> cb.greaterThanOrEqualTo(root.get("size"), filter.minSize()))

                .withCondition(Objects.nonNull(filter.maxPrice()),
                        (root, cb) -> cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()))

                .build();
    }
}