package com.task_management.Task.Management.services;

import com.task_management.Task.Management.config.JwtConfig;
import com.task_management.Task.Management.entities.User;
import com.task_management.Task.Management.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user){
        final long tokenExperation = jwtConfig.getAccessTokenExpiration(); //5 min
        return generateToken(user, tokenExperation);
    }

    public String generateRefreshToken(User user){
        final long tokenExperation = jwtConfig.getRefreshTokenExpiration(); //7 days
        return generateToken(user, tokenExperation);
    }

    private String generateToken(User user, long tokenExperation) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExperation))
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    public boolean validateToken(String token){
        try{
            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token){
        return Long.valueOf(getClaims(token).getSubject());
    }

    public Role getRoleFromToken(String token){
        return Role.valueOf(getClaims(token).get("role" , String.class));
    }

    public String getEmailFromToken(String token){
        return getClaims(token).get("email" , String.class);
    }
}
