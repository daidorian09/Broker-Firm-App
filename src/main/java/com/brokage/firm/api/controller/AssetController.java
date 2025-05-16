package com.brokage.firm.api.controller;

import com.brokage.firm.application.constant.ApplicationConstant;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.dto.request.AssetFilterRequest;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.domain.entity.Asset;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @Operation(summary = "List assets for a customer",
            description = "Retrieves a paginated list of assets for a customer, with optional filters for asset name, minimum usable size, and minimum total size.",
            security = {@SecurityRequirement(name = "basicAuth")})
    @GetMapping
    public ResponseEntity<Page<Asset>> listAssets(
            @ParameterObject final AssetFilterRequest request,
            @ParameterObject @PageableDefault(size = ApplicationConstant.DEFAULT_PAGE_SIZE,
                    sort = ApplicationConstant.DEFAULT_SORTING_FIELD,
                    direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(assetService
                .listAssets(new AssetFilter(request.customerId(),
                        request.assetName(),
                        request.minUsableSize(),
                        request.minTotalSize()), pageable));
    }
}