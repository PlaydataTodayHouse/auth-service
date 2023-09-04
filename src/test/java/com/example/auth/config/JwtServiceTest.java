package com.example.auth.config;

import com.example.auth.domain.entity.Role;
import com.example.auth.domain.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {
    @Autowired
    JwtService jwtService;
    UUID uuid = UUID.randomUUID();
    User dsa = User.builder().id(uuid).number("01").name("dsa").role(Role.CUSTOMER).build();
    @Test
    void makeToken() {




    }

    @Test
    void parseToken() {
        String token = jwtService.makeToken(dsa);
        TokenInfo tokenInfo = jwtService.parseToken(token);
        Assertions.assertEquals(tokenInfo.getId(),uuid);
        Assertions.assertEquals(tokenInfo.getRole(),dsa.getRole().name());
        Assertions.assertEquals(tokenInfo.getNumber(),dsa.getNumber());
        Assertions.assertEquals(tokenInfo.getName(),dsa.getName());
    }
}