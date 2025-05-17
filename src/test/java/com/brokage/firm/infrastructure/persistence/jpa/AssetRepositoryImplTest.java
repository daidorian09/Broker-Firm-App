package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaAssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetRepositoryImplTest {

    @Mock
    private JpaAssetRepository jpaAssetRepository;

    private AssetRepositoryImpl assetRepository;

    @BeforeEach
    void setUp() {
        assetRepository = new AssetRepositoryImpl(jpaAssetRepository);
    }

    @Test
    void findByFilters_shouldReturnMappedAssets() {
        final UUID customerId = UUID.randomUUID();
        final AssetFilter filter = new AssetFilter(customerId, "TRY", null, null);
        final Pageable pageable = PageRequest.of(0, 10);

        final AssetEntity entity = AssetEntity.builder()
                .customerId(customerId)
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build();

        when(jpaAssetRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        Page<Asset> result = assetRepository.findByFilters(filter, pageable);

        assertThat(result.getSize()).isOne();
        assertThat(result.getContent().get(0).getAssetName()).isEqualTo("TRY");
    }

    @Test
    void findByCustomerIdAndAssetName_shouldReturnMappedAsset() {
        final UUID customerId = UUID.randomUUID();
        final String assetName = "TRY";

        AssetEntity entity = AssetEntity.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build();

        when(jpaAssetRepository.findByCustomerIdAndAssetName(customerId, assetName))
                .thenReturn(Optional.of(entity));

        Optional<Asset> result = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);

        assertThat(result).isPresent();
        assertThat(result.get().getAssetName()).isEqualTo("TRY");
    }

    @Test
    void save_shouldCallJpaRepositoryWithMappedEntity() {
        final Asset domainAsset = Asset.builder()
                .customerId(UUID.randomUUID())
                .assetName("BTC")
                .totalSize(BigDecimal.TEN)
                .usableSize(BigDecimal.TEN)
                .build();

        assetRepository.save(domainAsset);

        verify(jpaAssetRepository).save(ArgumentMatchers.any(AssetEntity.class));
    }
}