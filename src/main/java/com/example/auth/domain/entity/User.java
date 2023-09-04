package com.example.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "users")
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String userId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Role role;
}
