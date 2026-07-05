package com.news.service.impl;

import com.news.dto.LoginRequest;
import com.news.dto.LoginResponse;
import com.news.entity.User;
import com.news.exception.BusinessException;
import com.news.repository.UserRepository;
import com.news.service.AuthService;
import com.news.util.JwtUtil;
import com.news.util.TokenBlacklist;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String key = request.getUsername();

        LoginAttempt attempt = loginAttempts.get(key);
        if (attempt != null && attempt.isLocked()) {
            throw new BusinessException("账户已被锁定，请15分钟后再试");
        }

        User user = userRepository.findByUsername(key)
                .orElseThrow(() -> {
                    recordFailedAttempt(key);
                    return new BusinessException("用户名或密码错误");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            recordFailedAttempt(key);
            throw new BusinessException("用户名或密码错误");
        }

        loginAttempts.remove(key);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.add(token);
    }

    private void recordFailedAttempt(String key) {
        loginAttempts.compute(key, (k, v) -> {
            if (v == null || v.isExpired()) {
                return new LoginAttempt(1, System.currentTimeMillis());
            }
            v.count++;
            return v;
        });
    }

    private static class LoginAttempt {
        int count;
        long firstAttemptTime;

        LoginAttempt(int count, long firstAttemptTime) {
            this.count = count;
            this.firstAttemptTime = firstAttemptTime;
        }

        boolean isLocked() {
            return count >= MAX_ATTEMPTS && !isExpired();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - firstAttemptTime > TimeUnit.MINUTES.toMillis(LOCK_DURATION_MINUTES);
        }
    }
}
