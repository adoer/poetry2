package com.poetry.service.impl;

import com.poetry.dto.SignupRequest;
import com.poetry.entity.User;
import com.poetry.exception.BusinessException;
import com.poetry.repository.UserRepository;
import com.poetry.repository.VerificationTokenRepository;
import com.poetry.service.EmailService;
import com.poetry.util.CaptchaCache;
import com.poetry.util.JwtUtil;
import com.poetry.util.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private TokenBlacklist tokenBlacklist;
    @Mock
    private CaptchaCache captchaCache;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil, tokenBlacklist, captchaCache, verificationTokenRepository, emailService);
    }

    @Test
    void signup_shouldSucceed_whenValidRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("abcd");

        when(captchaCache.get("abcd")).thenReturn("1");
        when(userRepository.findByUsername("13800138000")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(any(), eq("13800138000"), eq("USER"))).thenReturn("mock-token");

        var response = authService.signup(request);

        assertNotNull(response);
        assertEquals("13800138000", response.getUsername());
        assertEquals("mock-token", response.getToken());
        assertFalse(response.isBindtel());

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("13800138000", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("Password123", savedUser.getPassword()));
        assertEquals("13800138000", savedUser.getNickname());
        assertEquals("0", savedUser.getVipLevel());
        assertNotNull(savedUser.getId());
        assertEquals(16, savedUser.getId().length());
    }

    @Test
    void signup_shouldThrow_whenCaptchaExpired() {
        SignupRequest request = new SignupRequest();
        request.setUsername("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("wrong");

        when(captchaCache.get("wrong")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.signup(request));
        assertEquals("验证码不对或过期请重试", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_shouldThrow_whenUsernameExists() {
        SignupRequest request = new SignupRequest();
        request.setUsername("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("abcd");

        when(captchaCache.get("abcd")).thenReturn("1");
        when(userRepository.findByUsername("13800138000")).thenReturn(Optional.of(new User()));

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.signup(request));
        assertEquals("用户名已存在", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_shouldConsumeCaptchaAfterSuccess() {
        SignupRequest request = new SignupRequest();
        request.setUsername("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("abcd");

        when(captchaCache.get("abcd")).thenReturn("1");
        when(userRepository.findByUsername("13800138000")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("token");

        authService.signup(request);

        verify(captchaCache).remove("abcd");
    }

    @Test
    void login_shouldSucceed_whenValidCredentials() {
        var request = new com.poetry.dto.LoginRequest();
        request.setUsername("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("abcd");

        User user = new User();
        user.setUsername("13800138000");
        user.setPassword(passwordEncoder.encode("Password123"));
        user.setId("test-id-123");

        when(captchaCache.get("abcd")).thenReturn("1");
        when(userRepository.findByUsername("13800138000")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test-id-123", "13800138000", "USER")).thenReturn("login-token");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("13800138000", response.getUsername());
        assertEquals("login-token", response.getToken());
    }

    @Test
    void modifyPassword_shouldSucceed_whenValidCredentials() {
        String username = "13800138000";
        String oldPassword = "OldPass123";
        String newPassword = "NewPass456";

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(oldPassword));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        authService.modifyPassword(username, oldPassword, newPassword);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(passwordEncoder.matches(newPassword, savedUser.getPassword()));
    }

    @Test
    void modifyPassword_shouldThrow_whenUserNotFound() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.modifyPassword(username, "old", "new"));
        assertEquals("找不到此账号", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void modifyPassword_shouldThrow_whenOldPasswordWrong() {
        String username = "13800138000";
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("CorrectOld1"));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.modifyPassword(username, "WrongOld1", "NewPass456"));
        assertEquals("原密码不对", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldThrow_whenWrongPassword() {
        var request = new com.poetry.dto.LoginRequest();
        request.setUsername("13800138000");
        request.setPassword("WrongPass1");
        request.setVerificationCode("abcd");

        User user = new User();
        user.setUsername("13800138000");
        user.setPassword(passwordEncoder.encode("CorrectPass1"));

        when(captchaCache.get("abcd")).thenReturn("1");
        when(userRepository.findByUsername("13800138000")).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertEquals("密码不正确", ex.getMessage());
    }
}
