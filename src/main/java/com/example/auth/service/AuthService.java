package com.example.auth.service;

import com.example.auth.config.JwtService;
import com.example.auth.domain.entity.Promotion;
import com.example.auth.domain.entity.RefreshToken;
import com.example.auth.domain.entity.Role;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.request.LoginRequest;
import com.example.auth.domain.request.PromotionRequest;
import com.example.auth.domain.request.SignupRequest;
import com.example.auth.domain.response.LoginResponse;
import com.example.auth.domain.response.PromotionResponse;
import com.example.auth.domain.response.UserResponse;
import com.example.auth.exception.InvalidPasswordException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.repository.PromotionRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PromotionRepository promotionRepository;



    @Transactional
    public void signUp(SignupRequest request) {
        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .birth(request.getBirth())
                .profileImage(request.getProfileImage())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
    }


    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUserId(request.getUserId());
        User user = optionalUser.orElseThrow(
                () -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        String accessToken = jwtService.makeAccessToken(user);
        String refreshTokenString = jwtService.makeRefreshToken(user);

        // 리프레시 토큰 저장
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId((user.getUserId()));
        refreshToken.setToken(refreshTokenString);
        jwtService.saveRefreshToken(user, refreshTokenString);

        return new LoginResponse(accessToken, refreshTokenString);
    }




    // 아이디로 회원 정보 조회
    public UserResponse findUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND FOR USERID: " + userId));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .email(user.getEmail())
                .birth(user.getBirth())
                .profileImage(user.getProfileImage())
                .role(user.getRole().name())
                .build();
    }



    @Transactional
    public void requestPromotion(PromotionRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("없는 유저입니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다");
        }

        Promotion promotion = Promotion.builder()
                .user(user)
                .sellerName(request.getSellerName())
                .sellerNumber(request.getSellerNumber())
                .approved(false)
                .build();
        promotionRepository.save(promotion);
    }

    @Transactional
    public void approvePromotion(Long requestId) {
        Promotion promotion = promotionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청된 승급 신청이 없습니다"));

        User user = promotion.getUser();
        user.setRole(Role.SELLER);
        user.setSellerName(promotion.getSellerName());
        user.setSellerNumber(promotion.getSellerNumber());

        promotion.setApproved(true);
        userRepository.save(user);
        promotionRepository.save(promotion);
    }

    public List<PromotionResponse> getAllPromotionRequests() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream()
                .map(promotion -> new PromotionResponse(
                        promotion.getId(),
                        promotion.getUser().getUserId(), // Assuming User has getUserId() method.
                        promotion.getSellerName(),
                        promotion.getSellerNumber()))
                .collect(Collectors.toList());
    }


    // 서비스 호출 응답 검사 메서드
    private void checkServiceResponse(Role role, ResponseEntity<Void> response) {
        if (response.getStatusCode() != HttpStatus.CREATED) {
            String err = role.name() + "-SERVICE DEAD";
            throw new RuntimeException(err);
        }
    }
}
