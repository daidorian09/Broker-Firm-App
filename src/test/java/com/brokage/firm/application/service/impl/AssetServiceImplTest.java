package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.domain.exception.AssetDoesNotBelongToCustomerException;
import com.brokage.firm.domain.exception.DisallowedAssetException;
import com.brokage.firm.domain.exception.UnauthorizedAccessException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private LockService lockService;
    @Mock
    private BrokerApplicationConfig appConfig;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserPrincipal userPrincipal;

    @InjectMocks
    private AssetServiceImpl assetService;

    private final UUID customerId = UUID.randomUUID();
    private final String assetName = "TRY";
    private final BigDecimal amount = BigDecimal.TEN;

    @Test
    void listAssets_shouldDelegateToRepository_WhenUserIsAuthenticatedAsAdmin() {
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

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        Page<Asset> result = assetService.listAssets(filter, pageable);

        assertThat(result).isEqualTo(mockPage);
        verify(assetRepository).findByFilters(filter, pageable);
    }

    @Test
    void listAssets_shouldDelegateToRepository_WhenUserIsAuthenticatedAsCustomer() {
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

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(userPrincipal.customerId()).thenReturn(customerId);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        Page<Asset> result = assetService.listAssets(filter, pageable);

        assertThat(result).isEqualTo(mockPage);
        verify(assetRepository).findByFilters(filter, pageable);
    }

    @Test
    void listAssets_shouldThrowException_WhenAssetNotBelongToCustomer() {
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

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(userPrincipal.customerId()).thenReturn(UUID.randomUUID());
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        assertThatThrownBy(() ->
                assetService.listAssets(filter, pageable))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining( "Access denied. Resource does not belong to current user.");
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
    void reserveAsset_shouldThrowException_whenAssetNotFound() {
        UUID customerId = UUID.randomUUID();
        String assetName = "TRY";

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.reserveAsset(customerId, assetName, BigDecimal.TEN))
                .isInstanceOf(AssetDoesNotBelongToCustomerException.class)
                .hasMessageContaining(customerId.toString())
                .hasMessageContaining(assetName);

        verify(lockService, never()).executeWithLock(anyString(), any(Callable.class));
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
                .isInstanceOf(DisallowedAssetException.class)
                .hasMessageContaining("Asset creation is not allowed for: TRY");
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