package com.example.auth.domain.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionRequest {
    private String userId;
    private String password;
    private String sellerName;
    private String sellerNumber;
}
