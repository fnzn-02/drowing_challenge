package com.example.backend.controller;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.SignupDto;
import com.example.backend.entity.User;
//import com.example.backend.service.EmailService;
import com.example.backend.service.EmailService;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session) {
        User user = userService.login(loginDto);
        session.setAttribute("loginUser", user);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        emailService.sendVerificationEmail(request.get("email"));
        return ResponseEntity.ok("인증코드가 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        boolean result = emailService.verifyCode(request.get("email"), request.get("code"));
        return result
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.badRequest().body("인증 실패");
    }
}
