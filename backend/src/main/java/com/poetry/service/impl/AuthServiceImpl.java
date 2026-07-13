package com.poetry.service.impl;

import com.poetry.dto.LoginRequest;
import com.poetry.dto.LoginResponse;
import com.poetry.dto.SignupRequest;
import com.poetry.entity.User;
import com.poetry.exception.BusinessException;
import com.poetry.repository.UserRepository;
import com.poetry.service.AuthService;
import com.poetry.util.CaptchaCache;
import com.poetry.util.JwtUtil;
import com.poetry.util.TokenBlacklist;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final CaptchaCache captchaCache;

    public AuthServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil,
                            TokenBlacklist tokenBlacklist,
                            CaptchaCache captchaCache) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.captchaCache = captchaCache;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validateCaptcha(request.getVerificationCode());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("账号不存在，请注册。"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码不正确");
        }

        captchaCache.remove(request.getVerificationCode());

        boolean bindwx = user.getOpenid() != null && !user.getOpenid().isEmpty();
        boolean bindtel = user.getPhone() != null && user.getPhone().length() == 11;

        String role = "admin".equals(user.getUsername()) ? "ADMIN" : "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        return new LoginResponse(token, user.getUsername(), bindwx, bindtel);
    }

    @Override
    @Transactional
    public LoginResponse signup(SignupRequest request) {
        validateCaptcha(request.getVerificationCode());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        captchaCache.remove(request.getVerificationCode());

        String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        User user = new User();
        user.setId(userId);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getUsername());
        user.setLastLoginId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        user.setVipLevel("0");
        user.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        return new LoginResponse(token, user.getUsername(), false, false);
    }

    @Override
    @Transactional
    public void modifyPassword(String username, String passwordOld, String passwordNew) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("找不到此账号"));

        if (!passwordEncoder.matches(passwordOld, user.getPassword())) {
            throw new BusinessException("原密码不对");
        }

        user.setPassword(passwordEncoder.encode(passwordNew));
        userRepository.save(user);
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.add(token);
    }

    private void validateCaptcha(String code) {
        if (code == null || captchaCache.get(code) == null) {
            throw new BusinessException("验证码不对或过期请重试");
        }
    }
}
