package com.example.auth.config;

import com.example.auth.domain.entity.User;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;


    //access 토큰 유효시간 20분
    public String makeAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId().toString());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * 20)))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    // refresh 토큰 유효시간 3일
    public String makeRefreshToken(User user) {
        Date expiryDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 3));
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }


    // 수정 예정
    public Date getRefreshTokenExpiryDate(String refreshToken) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return body.getExpiration();
    }

    public TokenInfo parseAccessToken(String token) {
        Claims body = (Claims) Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parse(token)
                .getBody();
        UUID userId = UUID.fromString(body.get("id", String.class));

        User user = userRepository.findById(userId).orElse(null);

        if(user == null) {
            throw new RuntimeException("Invalid Token");
        }

        return TokenInfo.builder()
                .id(userId)
                .userId(user.getUserId())
                .name(user.getName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .birth(user.getBirth())
                .role(user.getRole().name())
                .build();
    }
}
