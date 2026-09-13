package com.example.usermanagementservice.repos;

import com.example.usermanagementservice.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<UserSession,Long> {
    Optional<UserSession> findByTokenAndUserId(String token, Long userId);
}
