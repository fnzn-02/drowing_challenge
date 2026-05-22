package com.example.backend.controller;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.SignupDto;
import com.example.backend.entity.User;
import com.example.backend.service.EmailService;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입해주는 Lombok 어노테이션
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto){
        userService.signup(signupDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session){
        User user = userService.login(loginDto);
        // 세션에 로그인한 유저 저장
        session.setAttribute("loginUser", user);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        // 세션에 저장된 모든 데이터 삭제
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request){
        String email = request.get("email");
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok("인증코드가 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request){
        String email = request.get("email");
        String code = request.get("code");
        boolean result = emailService.verifyCode(email, code);
        if (result) {
            return ResponseEntity.ok("인증 성공");
        } else{
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }
}
