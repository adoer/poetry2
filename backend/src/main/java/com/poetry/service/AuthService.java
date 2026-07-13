package com.poetry.service;

import com.poetry.dto.LoginRequest;
import com.poetry.dto.LoginResponse;
import com.poetry.dto.SignupRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse signup(SignupRequest request);
    void modifyPassword(String username, String passwordOld, String passwordNew);
    void logout(String token);
    void sendVerificationEmail(String username, String email);
    void verifyEmail(String username, String token);
    void forgotPassword(String email, String code, String newPassword);
    void forgotSendCode(String email);
}
