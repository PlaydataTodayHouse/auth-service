package com.example.auth.repository;

import com.example.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository
    extends JpaRepository<User, UUID> {
    Optional<User> findByUserId(String userId);

    Optional<User> findByUserUUId(String userUUId);
}
