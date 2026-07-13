package com.poetry.dto;

import com.poetry.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignupRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String username;

    @NotBlank(message = "密码不能为空")
    @StrongPassword
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    @Pattern(regexp = "^$|^[\\w.-]+@[\\w.-]+\\.\\w{2,}$", message = "邮箱格式不正确")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
