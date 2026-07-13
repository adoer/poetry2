package com.poetry.controller;

import com.poetry.dto.*;
import com.poetry.service.AuthService;
import com.poetry.util.CaptchaCache;
import com.poetry.util.CaptchaGenerator;
import com.poetry.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final long CAPTCHA_TTL = 5 * 60 * 1000;

    private final AuthService authService;
    private final CaptchaGenerator captchaGenerator;
    private final CaptchaCache captchaCache;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, CaptchaGenerator captchaGenerator,
                          CaptchaCache captchaCache, JwtUtil jwtUtil) {
        this.authService = authService;
        this.captchaGenerator = captchaGenerator;
        this.captchaCache = captchaCache;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        String code = captchaGenerator.generateCode();
        String image = captchaGenerator.generateBase64Image(code);
        captchaCache.put(code, "1", CAPTCHA_TTL);
        return Result.success(Map.of("image", image));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/signup")
    public Result<LoginResponse> signup(@Valid @RequestBody SignupRequest request) {
        return Result.success(authService.signup(request));
    }

    @PutMapping("/profile/password")
    public Result<Void> modifyPassword(@RequestHeader("Authorization") String token,
                                        @Valid @RequestBody ModifyPasswordRequest request) {
        String username = jwtUtil.getUsername(token.substring(7));
        authService.modifyPassword(username, request.getPasswordOld(), request.getPasswordNew());
        return Result.success();
    }

    @PostMapping("/send-verification-email")
    public Result<Void> sendVerificationEmail(@RequestHeader("Authorization") String token,
                                              @RequestBody Map<String, String> body) {
        String username = jwtUtil.getUsername(token.substring(7));
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return Result.error("邮箱不能为空");
        }
        authService.sendVerificationEmail(username, email);
        return Result.success();
    }

    @PostMapping("/verify-email")
    public Result<Void> verifyEmail(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, String> body) {
        String username = jwtUtil.getUsername(token.substring(7));
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return Result.error("验证码不能为空");
        }
        authService.verifyEmail(username, code);
        return Result.success();
    }

    @PostMapping("/forgot/send-code")
    public Result<Void> forgotSendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return Result.error("邮箱不能为空");
        }
        authService.forgotSendCode(email);
        return Result.success();
    }

    @PostMapping("/forgot/reset")
    public Result<Void> forgotReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String password = body.get("password");
        if (email == null || code == null || password == null) {
            return Result.error("参数不完整");
        }
        authService.forgotPassword(email, code, password);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token.substring(7));
        }
        return Result.success();
    }
}
