package com.example.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "users")
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
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;
}
