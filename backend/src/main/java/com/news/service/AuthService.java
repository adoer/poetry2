package com.news.service;

import com.news.dto.LoginRequest;
import com.news.dto.LoginResponse;
import com.news.dto.SignupRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse signup(SignupRequest request);
    void modifyPassword(String username, String passwordOld, String passwordNew);
    void logout(String token);
}
