package com.example.auth.client.request;

import java.time.LocalDate;
import java.util.UUID;

public record OwnerRequest(
        UUID id,
        String name,
        String phoneNumber,
        String userId,
        String email,
        LocalDate birth
) {
}
