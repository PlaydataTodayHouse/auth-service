package com.example.auth.config;

import com.example.auth.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String makeToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId().toString());
        claims.put("userId", user.getUserId());
        claims.put("name", user.getName());
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("email", user.getEmail());
        claims.put("birth", user.getBirth());
        claims.put("address", user.getAddress());
        claims.put("role", user.getRole().name());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setExpiration(new Date(
                        System.currentTimeMillis()
                                + (1000L * 60 * 60 * 24 * 30)
                ))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
        return token;
    }
    public TokenInfo parseToken(String token){
        Claims body = (Claims) Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parse(token)
                .getBody();
        return TokenInfo.builder()
                .id(UUID.fromString(body.get("id", String.class)))
                .userId(body.get("userId", String.class))
                .name(body.get("name", String.class))
                .phoneNumber(body.get("phoneNumber", String.class))
                .email(body.get("email", String.class))
                .birth(LocalDate.parse(body.get("birth", String.class)))
                .role(body.get("role", String.class))
                .build();
    }


}
