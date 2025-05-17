package com.brokage.firm.api.controller;

import com.brokage.firm.application.configuration.SecurityConfig;
import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.domain.entity.Asset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetController.class)
@Import(SecurityConfig.class)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @Test
    void listAssets_shouldReturnPagedAssets() throws Exception {
        final UUID customerId = UUID.randomUUID();
        final Asset mockAsset = Asset.builder()
                .customerId(customerId)
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build();

        Page<Asset> page = new PageImpl<>(List.of(mockAsset));
        when(assetService.listAssets(any(AssetFilter.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/assets")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createDate,DESC")
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].assetName").value("TRY"))
                .andExpect(jsonPath("$.content[0].usableSize").value(80));
    }

    @Test
    void listAssets_shouldReturn403_whenAuthorizationIsMissing() throws Exception {
        final UUID customerId = UUID.randomUUID();

        mockMvc.perform(get("/api/assets")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createDate,DESC"))
                .andExpect(status().isForbidden());
    }
}
