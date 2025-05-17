package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.domain.service.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private LockService lockService;
    @Mock
    private BrokerApplicationConfig appConfig;

    @InjectMocks
    private AssetServiceImpl assetService;

    private final UUID customerId = UUID.randomUUID();
    private final String assetName = "TRY";
    private final BigDecimal amount = BigDecimal.TEN;

    @Test
    void listAssets_shouldDelegateToRepository() {
        final AssetFilter filter = new AssetFilter(customerId, assetName, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Asset mockAsset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(50))
                .build();

        Page<Asset> mockPage = new PageImpl<>(Collections.singletonList(mockAsset));
        when(assetRepository.findByFilters(filter, pageable)).thenReturn(mockPage);

        // Act
        Page<Asset> result = assetService.listAssets(filter, pageable);

        // Assert
        assertThat(result).isEqualTo(mockPage);
        verify(assetRepository).findByFilters(filter, pageable);
    }


    @Test
    void reserveAsset_shouldCallRepositoryAndSaveReservedAsset() {
        final Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(50))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(lockService).executeWithLock(anyString(), any(Runnable.class));

        assetService.reserveAsset(customerId, assetName, amount);

        verify(assetRepository).save(any(Asset.class));
    }

    @Test
    void creditOrCreateCustomerAsset_shouldCreateNewAssetWhenAbsent() {
        when(appConfig.getCurrencies()).thenReturn(Collections.singletonList(assetName));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(lockService).executeWithLock(anyString(), any(Runnable.class));

        assetService.creditOrCreateCustomerAsset(customerId, assetName, amount);

        verify(assetRepository).save(any(Asset.class));
    }

    @Test
    void creditOrCreateCustomerAsset_shouldThrowExceptionIfCurrencyNotAllowed() {
        when(appConfig.getCurrencies()).thenReturn(Collections.singletonList("USD"));

        assertThatThrownBy(() ->
                assetService.creditOrCreateCustomerAsset(customerId, assetName, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("auto-create not allowed");
    }

    @Test
    void releaseReservedAsset_shouldUpdateUsableSize() {
        final Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(30))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(lockService).executeWithLock(anyString(), any(Runnable.class));

        assetService.releaseReservedAsset(customerId, assetName, amount);

        verify(assetRepository).save(any(Asset.class));
    }
}