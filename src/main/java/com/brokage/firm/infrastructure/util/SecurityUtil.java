package com.brokage.firm.infrastructure.util;

import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.domain.exception.UnauthorizedAccessException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class SecurityUtil {
    public static UUID getCurrentCustomerId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            return principal.customerId();
        }

        throw new UnauthorizedAccessException("JWT user not found in context");
    }

    public static boolean isAdmin() {
        final Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(SecurityConstant.AUTHORIZATION_ROLE + UserRole.ADMIN));
    }

    public static void assertOwnershipOrAdmin(UUID resourceOwnerId) {
        if (isAdmin()) return;

        final UUID currentUserId = getCurrentCustomerId();

        if (!Objects.equals(currentUserId, resourceOwnerId)) {
            throw new UnauthorizedAccessException("Access denied. Resource does not belong to current user.");
        }
    }
}