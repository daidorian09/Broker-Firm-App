package com.brokage.firm.infrastructure.util;

import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.domain.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserPrincipal userPrincipal;

    @BeforeEach
    void setup() {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void shouldReturnCustomerId_whenPrincipalIsJwtUser() {
        final UUID userId = UUID.randomUUID();
        when(userPrincipal.customerId()).thenReturn(userId);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        final UUID result = SecurityUtil.getCurrentCustomerId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void shouldThrow_whenPrincipalIsNotJwtUser() {
        when(authentication.getPrincipal()).thenReturn("admin");

        assertThatThrownBy(SecurityUtil::getCurrentCustomerId)
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("JWT user not found");
    }

    @Test
    void shouldReturnTrue_whenUserIsAdmin() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        final boolean result = SecurityUtil.isAdmin();

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenUserIsNotAdmin() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        final boolean result = SecurityUtil.isAdmin();

        assertThat(result).isFalse();
    }


    @Test
    void shouldNotThrow_whenAdminChecksOwnership() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);

        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        assertThatCode(() ->
                SecurityUtil.assertOwnershipOrAdmin(UUID.randomUUID())
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenOwnerChecksOwnResource() {
        final UUID userId = UUID.randomUUID();
        when(userPrincipal.customerId()).thenReturn(userId);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        assertThatCode(() ->
                SecurityUtil.assertOwnershipOrAdmin(userId)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldThrow_whenCustomerAccessesOtherResource() {
        final UUID userId = UUID.randomUUID();
        final UUID anotherId = UUID.randomUUID();
        when(userPrincipal.customerId()).thenReturn(userId);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        final Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);

        assertThatThrownBy(() ->
                SecurityUtil.assertOwnershipOrAdmin(anotherId)
        ).isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("Access denied");
    }
}