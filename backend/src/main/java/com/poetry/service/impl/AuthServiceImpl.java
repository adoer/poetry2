package com.poetry.service.impl;

import com.poetry.dto.LoginRequest;
import com.poetry.dto.LoginResponse;
import com.poetry.dto.SignupRequest;
import com.poetry.entity.User;
import com.poetry.entity.VerificationToken;
import com.poetry.exception.BusinessException;
import com.poetry.repository.UserRepository;
import com.poetry.repository.VerificationTokenRepository;
import com.poetry.service.AuthService;
import com.poetry.service.EmailService;
import com.poetry.util.CaptchaCache;
import com.poetry.util.JwtUtil;
import com.poetry.util.TokenBlacklist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final CaptchaCache captchaCache;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil,
                            TokenBlacklist tokenBlacklist,
                            CaptchaCache captchaCache,
                            VerificationTokenRepository verificationTokenRepository,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.captchaCache = captchaCache;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
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

        boolean bindtel = user.getPhone() != null && user.getPhone().length() == 11;

        String role = "admin".equals(user.getVipLevel()) ? "ADMIN" : "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        return new LoginResponse(token, user.getUsername(), role, bindtel, user.getEmail());
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
        user.setPhone(request.getUsername());
        user.setLastLoginId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        user.setVipLevel("0");
        user.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        return new LoginResponse(token, user.getUsername(), "USER", false, user.getEmail());
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

        log.info("用户 {} 修改密码成功", username);
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.add(token);
    }

    @Override
    public void sendVerificationEmail(String username, String email) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        verificationTokenRepository.findByUsernameAndType(username, "email_verify")
                .ifPresent(t -> verificationTokenRepository.delete(t));

        String tokenId = UUID.randomUUID().toString();
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        LocalDateTime now = LocalDateTime.now();
        VerificationToken vt = new VerificationToken(
                tokenId, username, code, email,
                "email_verify", now, now.plusHours(1));
        verificationTokenRepository.save(vt);

        emailService.sendVerificationCode(email, code);
    }

    @Override
    @Transactional
    public void verifyEmail(String username, String code) {
        VerificationToken vt = verificationTokenRepository
                .findByUsernameAndType(username, "email_verify")
                .orElseThrow(() -> new BusinessException("未找到验证请求"));

        if (vt.isExpired()) {
            verificationTokenRepository.delete(vt);
            throw new BusinessException("验证码已过期，请重新发送");
        }

        if (vt.isVerified()) {
            throw new BusinessException("邮箱已验证");
        }

        if (!vt.getToken().equals(code)) {
            throw new BusinessException("验证码不正确");
        }

        vt.setVerifiedAt(LocalDateTime.now());
        verificationTokenRepository.save(vt);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setEmail(vt.getEmail());
        userRepository.save(user);

        log.info("用户 {} 邮箱 {} 绑定成功", username, vt.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(String email, String code, String newPassword) {
        VerificationToken vt = verificationTokenRepository
                .findByToken(code)
                .orElseThrow(() -> new BusinessException("验证码不正确"));

        if (!vt.getEmail().equals(email) || !"email_verify".equals(vt.getType())) {
            throw new BusinessException("验证码不正确");
        }

        if (vt.isExpired()) {
            verificationTokenRepository.delete(vt);
            throw new BusinessException("验证码已过期，请重新发送");
        }

        if (vt.isVerified()) {
            throw new BusinessException("该验证码已使用");
        }

        vt.setVerifiedAt(LocalDateTime.now());
        verificationTokenRepository.save(vt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("该邮箱未绑定账号"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("用户 {} 通过邮箱 {} 重置密码成功", user.getUsername(), email);
    }

    @Override
    @Transactional
    public void forgotSendCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("该邮箱未绑定账号"));

        verificationTokenRepository.findByUsernameAndType(user.getUsername(), "email_verify")
                .ifPresent(t -> verificationTokenRepository.delete(t));

        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        LocalDateTime now = LocalDateTime.now();
        VerificationToken vt = new VerificationToken(
                UUID.randomUUID().toString(), user.getUsername(), code, email,
                "email_verify", now, now.plusHours(1));
        verificationTokenRepository.save(vt);

        emailService.sendVerificationCode(email, code);
        log.info("找回密码验证码已发送至 {}", email);
    }

    private void validateCaptcha(String code) {
        if (code == null || captchaCache.get(code) == null) {
            throw new BusinessException("验证码不对或过期请重试");
        }
    }
}
