package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입해주는 Lombok 어노테이션
public class EmailService {
    private final JavaMailSender javaMailSender; // 이메일 발송 객체
    private final StringRedisTemplate redisTemplate; // Redis 저장/조회 객체

    public void sendVerificationEmail(String email){
        String code = String.format("%06d", new SecureRandom().nextInt(1000000));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // 받는사람
        message.setFrom("그림챌린지 <" + "luyanfndis@gmail.com" + ">"); // 보내는사람
        message.setSubject("그림 챌린지 이메일 인증"); // 제목
        message.setText("인증코드: " + code + "\n 10분 내에 인증해주세요. "); // 인증코드
        javaMailSender.send(message); // 메일 발송
        // 이메일 발송 후 10분 안에 인증코드 입력
        redisTemplate.opsForValue().set("EMAIL_CODE:" + email, code, 10, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String email, String code){
        String savedCode = redisTemplate.opsForValue().get("EMAIL_CODE:" + email);

        if(savedCode != null && savedCode.equals(code)){
            redisTemplate.delete("EMAIL_CODE:" + email); // 인증 완료 후 인증코드 삭제
            // 인증 완료 후 10분 안에 회원가입 완료
            redisTemplate.opsForValue().set("VERIFIED:" + email, "TRUE", 10, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    // Redis에 인증 완료 상태가 있는지 확인 (있으면 true, 없으면 false)쓰면
    public boolean isVerified(String email){
        return Boolean.TRUE.equals(redisTemplate.hasKey("VERIFIED:" + email));
    }
}
