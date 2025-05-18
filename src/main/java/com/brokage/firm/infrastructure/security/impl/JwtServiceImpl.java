package com.brokage.firm.infrastructure.security.impl;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.infrastructure.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final BrokerApplicationConfig brokerApplicationConfig;

    public String generateToken(final CustomUserPrincipal principal) {
        return Jwts.builder()
                .setSubject(principal.email())
                .claim("id", principal.customerId().toString())
                .claim("role", principal.role().name())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusDays(3)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(Keys.hmacShaKeyFor(brokerApplicationConfig.getSecurityConfig().getJwtSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public CustomUserPrincipal parseToken(final String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(brokerApplicationConfig.getSecurityConfig().getJwtSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new CustomUserPrincipal(
                UUID.fromString(claims.get("id", String.class)),
                claims.getSubject(),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }
}