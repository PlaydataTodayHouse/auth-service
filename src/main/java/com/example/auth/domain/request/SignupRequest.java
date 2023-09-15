package com.example.auth.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String userId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate birth;
    private String profileImage;
}
