package com.example.auth.config;

import com.example.auth.domain.entity.RefreshToken;
import com.example.auth.domain.entity.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public String refreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userId = claims.getSubject();
            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Refresh token is expired");
            }

            return makeAccessToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while refreshing token");
        }
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

    public void saveRefreshToken(User user, String refreshToken) {
        RefreshToken newToken = new RefreshToken();
        newToken.setUserId((user.getUserId()));
        newToken.setToken(refreshToken);
        newToken.setExpiryDate(getRefreshTokenExpiryDate(refreshToken));
        refreshTokenRepository.save(newToken);
    }

    public LocalDateTime getRefreshTokenExpiryDate(String refreshToken) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return body.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }


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
