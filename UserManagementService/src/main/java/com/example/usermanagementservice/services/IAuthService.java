package com.example.usermanagementservice.services;

import com.example.usermanagementservice.dtos.ValidateTokenRequestDto;
import com.example.usermanagementservice.models.User;
import com.example.usermanagementservice.repos.UserRepo;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


public interface IAuthService {

    User signup(String name, String email, String password, String phoneNumber);

    Pair<User, String> login(String username, String password);

    Boolean validateToken(String token, Long userId);

    User getProfile(Long userId, String token);

    User updateProfile(Long userId, String token, String name, String phoneNumber, String address);

    String forgotPassword(String email);

    void resetPassword(String token, String newPassword);
}
