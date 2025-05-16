package com.brokage.firm.infrastructure.persistence.jpa.spec;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class AssetSpecification {

    public static Specification<AssetEntity> byFilter(final AssetFilter filter) {
        return new GenericSpecificationBuilder<AssetEntity>()
                .withCondition(Objects.nonNull(filter.customerId()),
                        (root, cb) -> cb.equal(root.get("customerId"), filter.customerId()))

                .withCondition(StringUtils.isNotBlank(filter.assetName()),
                        (root, cb) -> cb.equal(cb.upper(root.get("assetName")), filter.assetName().toUpperCase()))

                .withCondition(Objects.nonNull(filter.minUsableSize()),
                        (root, cb) -> cb.greaterThanOrEqualTo(root.get("usableSize"), filter.minUsableSize()))

                .withCondition(Objects.nonNull(filter.minTotalSize()),
                        (root, cb) -> cb.greaterThanOrEqualTo(root.get("totalSize"), filter.minTotalSize()))

                .build();
    }
}