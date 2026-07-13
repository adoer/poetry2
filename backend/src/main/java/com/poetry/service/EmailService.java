package com.poetry.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);
}
