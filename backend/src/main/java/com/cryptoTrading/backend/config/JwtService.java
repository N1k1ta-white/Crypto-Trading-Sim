package com.cryptoTrading.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.term}")
    private long jwtTerm;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final String USER_ID_FIELD = "userId";

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);

        if (username == null || username.isEmpty()) {
            throw new BadCredentialsException("Invalid username in token");
        }

        return username;
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(USER_ID_FIELD, Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(USER_ID_FIELD, userId);
        return generateToken(userDetails, extraClaims);
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtTerm))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean tokenValidator(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String actualUsername = userDetails.getUsername();

        return username.equals(actualUsername) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}