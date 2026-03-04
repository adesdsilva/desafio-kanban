package br.com.setecolinas.kanban_project.security;

import br.com.setecolinas.kanban_project.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:mySuperSecretKeyThatIsAtLeast32CharactersLongForHS256}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas em ms
    private long jwtExpirationMs;

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("organizationId", user.getOrganization().getId())
                .claim("tenantId", user.getTenantId())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return (Long) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
    }

    public String getTenantIdFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tenantId");
    }

    public Long getOrganizationIdFromToken(String token) {
        return ((Number) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("organizationId")).longValue();
    }

    public String getRoleFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Instant getExpirationDate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .toInstant();
    }
}

