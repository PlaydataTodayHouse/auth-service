package com.example.auth.domain.response;

import com.example.auth.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PromotionResponse {
    private Long id;
    private String userId;
    private String sellerName;
    private String sellerNumber;
}
