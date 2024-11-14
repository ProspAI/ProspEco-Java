package br.com.fiap.jadv.prospeco.config;// JwtTokenProvider.java

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-in-ms}")
    private long jwtExpirationInMs;

    private Key key;

    @PostConstruct
    public void init() {
        // Gera a chave a partir da string secreta
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * @param username Nome de usuário (email).
     * @return Token JWT.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Recupera o username (email) do token JWT.
     *
     * @param token Token JWT.
     * @return Nome de usuário.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valida o token JWT.
     *
     * @param authToken Token JWT.
     * @return Verdadeiro se válido, falso caso contrário.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException ex) {
            // Log ou tratar a exceção conforme necessário
            return false;
        }
    }
}
