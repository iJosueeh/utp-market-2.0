package com.utpmarket.utp_market.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilidad para generar, validar y extraer información de tokens JWT.
 * Utiliza JJWT library con algoritmo HS256 para firma de tokens.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * Genera la clave secreta para firmar tokens desde el string base64
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el email (username) del token JWT
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT de acceso para el usuario
     * 
     * @param userDetails Detalles del usuario autenticado
     * @return Token JWT firmado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Agregar roles al token
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return createToken(claims, userDetails.getUsername(), expiration);
    }

    /**
     * Genera un refresh token para el usuario
     * 
     * @param userDetails Detalles del usuario autenticado
     * @return Refresh token JWT firmado con mayor duración
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    /**
     * Crea un token JWT con los claims, subject y tiempo de expiración
     * especificados
     */
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si el token es válido para el usuario dado
     * 
     * @param token       Token JWT a validar
     * @param userDetails Detalles del usuario
     * @return true si el token es válido y no ha expirado
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Valida si el token es válido (sin verificar usuario específico)
     * Útil para verificar refresh tokens
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido y no ha expirado
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el tiempo de expiración configurado para access tokens
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * Obtiene el tiempo de expiración configurado para refresh tokens
     */
    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}
