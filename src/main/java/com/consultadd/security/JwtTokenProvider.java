package com.consultadd.security;

import com.consultadd.exceptions.ApplicationException;
import com.consultadd.model.User;
import com.consultadd.model.dto.AuthResponse;
import com.consultadd.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String jwtSecret;

    private final int jwtExpirationInMs;
    private final UserRepository repository;

    @Autowired
    public JwtTokenProvider(
            UserRepository repository,
            @Value("${jwt.ExpirationInMs}") int jwtExpirationInMs,
            @Value("${jwt.Secret}") String jwtSecret) {
        this.repository = repository;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    private AuthResponse generateToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", userPrincipal.getClientId());
        claims.put("role", userPrincipal.getAuthorities());

        String jwt =
                Jwts.builder()
                        .setSubject(Long.toString(userPrincipal.getId()))
                        .addClaims(claims)
                        .setIssuedAt(new Date())
                        .setExpiration(expiryDate)
                        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                        .compact();
        return AuthResponse.builder()
                .accessToken(jwt)
                .expiration(Long.toString(expiryDate.toInstant().toEpochMilli()))
                .build();
    }

    public AuthResponse generateTokenFromAuthentication(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal);
    }

    public AuthResponse generateTokenFromUserId(Long id) {

        User user =
                repository
                        .findById(id)
                        .orElseThrow(() -> new ApplicationException("User not found for Id " + id));
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        return generateToken(userPrincipal);
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
