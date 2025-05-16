package com.brokage.firm.infrastructure.persistence.jpa.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class GenericSpecificationBuilder<T> {

    private final List<BiFunction<Root<T>, CriteriaBuilder, Predicate>> criteriaList = new ArrayList<>();

    public GenericSpecificationBuilder<T> withCondition(final boolean condition,
                                                        BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateSupplier) {
        if (condition) {
            criteriaList.add(predicateSupplier);
        }
        return this;
    }

    public Specification<T> build() {
        return (root, query, cb) -> cb.and(criteriaList
                .stream()
                .map(func -> func
                        .apply(root, cb)).
                toArray(Predicate[]::new));
    }
}