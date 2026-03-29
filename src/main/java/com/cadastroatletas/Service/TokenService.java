package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Professor; // AJUSTADO: Import do Professor
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenService {

    @Value("${api.security.token.secret:minha-chave-secreta-muito-forte-do-ct-ferroviario-2026}")
    private String secret;

    // Gera o Token para o Professor (Sensei)
    public String generateToken(Professor professor) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(professor.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida o Token e extrai o e-mail (subject)
    public String validateToken(String token) {
        try {
            // Se o token for inválido, o extractClaim vai estourar erro e cair no catch
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            return "";
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}