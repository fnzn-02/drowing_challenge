package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "EMAIL_CODE:";
    private static final String VERIFIED_PREFIX = "VERIFIED:";

    public void sendVerificationEmail(String email) {
        String code = String.format("%06d", new SecureRandom().nextInt(1000000));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("luyanfndis@gmail.com");
        message.setSubject("그림 챌린지 이메일 인증");
        message.setText("인증코드: " + code + "\n10분 내에 인증해주세요.");
        javaMailSender.send(message);

        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, 10, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);
        if (savedCode != null && savedCode.equals(code)) {
            redisTemplate.delete(CODE_PREFIX + email);
            redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "TRUE", 10, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(VERIFIED_PREFIX + email));
    }
}
