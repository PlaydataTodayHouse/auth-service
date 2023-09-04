package com.example.auth.service;

import com.example.auth.client.request.OwnerRequest;
import com.example.auth.config.JwtService;
import com.example.auth.domain.entity.Role;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.request.SignupRequest;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OwnerClient ownerClient;
    private final CustomerClient customerClient;

    @Transactional
    public void signUp(SignupRequest request) {
        User user = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber()) // "number"를 "phoneNumber"로 수정
                .email(request.getEmail())
                .birth(request.getBirth()) // 생년월일 추가
                .role(Role.valueOf(request.getRole()))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        // 역할에 따라 서비스 호출
        if (savedUser.getRole() == Role.OWNER) {
            OwnerRequest ownerRequest = new OwnerRequest(
                    savedUser.getId(), savedUser.getName(), savedUser.getPhoneNumber());
            ResponseEntity<Void> response = ownerClient.saveOwner(ownerRequest);
            checkServiceResponse(savedUser.getRole(), response);
        } else if (savedUser.getRole() == Role.CUSTOMER) {
            CustomerRequest customerRequest = new CustomerRequest(
                    savedUser.getId(), savedUser.getName(), savedUser.getPhoneNumber());
            ResponseEntity<Void> response = customerClient.saveCustomer(customerRequest);
            checkServiceResponse(savedUser.getRole(), response);
        }
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUserId(request.getUserId());
        User user = optionalUser.orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("INVALID PASSWORD");
        }

        String token = jwtService.makeToken(user);
        return new LoginResponse(token, user.getRole().name());
    }

    // 서비스 호출 응답 검사 메서드
    private void checkServiceResponse(Role role, ResponseEntity<Void> response) {
        if (response.getStatusCode() != HttpStatus.CREATED) {
            String err = role.name() + "-SERVICE DEAD";
            throw new RuntimeException(err);
        }
    }
}
