package com.brokage.firm.infrastructure.persistence.jpa.spec;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaAssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AssetSpecificationTest {

    @Autowired
    private JpaAssetRepository assetRepository;

    @Test
    void shouldFilterByCustomerIdAndAssetName_andReturnCorrectResult() {
       final UUID customerId = UUID.randomUUID();

        assetRepository.save(AssetEntity.builder()
                .customerId(customerId)
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build());

        final AssetFilter filter = new AssetFilter(customerId, "try", null, null);
        final Specification<AssetEntity> spec = AssetSpecification.byFilter(filter);

        final Page<AssetEntity> result = assetRepository.findAll(spec, Pageable.ofSize(10));

        assertThat(result.getContent().size()).isOne();
        assertThat(result.getContent().get(0).getAssetName()).isEqualTo("TRY");
        assertThat(result.getContent().get(0).getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void shouldFilterByMinTotalSize() {
        assetRepository.save(AssetEntity.builder()
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build());

        final AssetFilter filter = new AssetFilter(null, null, null, BigDecimal.valueOf(100));
        final Specification<AssetEntity> spec = AssetSpecification.byFilter(filter);

        final Page<AssetEntity> result = assetRepository.findAll(spec, Pageable.ofSize(10));

        assertThat(result.getContent().size()).isOne();
        assertThat(result.getContent().get(0).getAssetName()).isEqualTo("TRY");
    }

    @Test
    void shouldFilterByMinUsableSize() {
        assetRepository.save(AssetEntity.builder()
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(200))
                .usableSize(BigDecimal.valueOf(150))
                .build());

        final AssetFilter filter = new AssetFilter(null, null, BigDecimal.valueOf(100), null);
        final Specification<AssetEntity> spec = AssetSpecification.byFilter(filter);

        final Page<AssetEntity> result = assetRepository.findAll(spec, Pageable.ofSize(10));

        assertThat(result.getContent().size()).isOne();
        assertThat(result.getContent().get(0).getAssetName()).isEqualTo("TRY");
    }
}