package com.example.ApSystem.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Clase responsable de:
 * 1️⃣ Generar JWTs seguros con clave de 256 bits (HS256).
 * 2️⃣ Validar tokens.
 * 3️⃣ Extraer el username (subject) de un token.
 */
@Component
public class JwtUtils {

    // 🔑 Clave secreta segura (256 bits) para HS256
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // ⏰ Duración del token: 1 hora
    private final long expirationMs = 1000 * 60 * 60;

    /**
     * Genera un JWT para un usuario autenticado.
     */
    public String generateToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key) // firma segura
                .compact();
    }

    /**
     * Extrae el username (subject) de un JWT.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }

    /**
     * Valida un token: devuelve true si la firma es correcta y el token no ha expirado.
     * Útil para filtros que solo quieren saber si el token es válido.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // lanza excepción si inválido
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida un token comparando username y expiración.
     * Útil cuando necesitas validar que el token pertenece a un usuario específico.
     */
    public boolean isTokenValid(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return (username.equals(tokenUsername) && expiration.after(new Date()));
        } catch (Exception e) {
            return false;
        }
    }
}
