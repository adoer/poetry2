package com.poetry.service.impl;

import com.poetry.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${spring.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    @Async
    public void sendVerificationCode(String to, String code) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(from, "拾光古诗文");
                helper.setTo(to);
                helper.setSubject("拾光古诗文 - 邮箱验证码");

                String html = buildHtml(code);
                helper.setText(html, true);

                mailSender.send(message);
                log.info("验证码邮件已发送至 {}", to);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("发送验证码邮件失败: {}", to, e);
            }
        });
    }

    private String buildHtml(String code) {
        String template = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#f5f0e6;font-family:'Noto Serif SC','SimSun','STSong',serif;">
              <table style="max-width:520px;margin:40px auto;background:#faf9f5;border-radius:12px;box-shadow:0 8px 30px rgba(0,0,0,0.1);padding:40px;">
                <tr>
                  <td style="text-align:center;padding-bottom:8px;">
                    <span style="display:inline-block;background:linear-gradient(135deg,#c23a2b 0%%,#8b2520 100%%);color:#fff;font-size:13px;padding:4px 12px;border-radius:4px;transform:rotate(-3deg);">拾光</span>
                  </td>
                </tr>
                <tr>
                  <td style="text-align:center;font-size:20px;color:#1a1a1a;padding:16px 0 8px;letter-spacing:1px;">邮箱验证码</td>
                </tr>
                <tr>
                  <td style="text-align:center;font-size:13px;color:#8b7355;padding-bottom:24px;">请使用以下验证码完成操作，验证码有效期为 1 小时</td>
                </tr>
                <tr>
                  <td style="text-align:center;">
                    <span style="display:inline-block;background:#f0efe2;border-radius:8px;padding:16px 32px;font-size:32px;letter-spacing:8px;color:#c23a2b;font-weight:bold;">%s</span>
                  </td>
                </tr>
                <tr>
                  <td style="text-align:center;padding-top:24px;font-size:12px;color:#8b7355;">
                    如果这不是你的操作，请忽略此邮件<br>拾光古诗文 — 诗书相伴，岁月静好
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """;
        return template.formatted(code);
    }
}
