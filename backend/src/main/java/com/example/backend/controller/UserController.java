package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getMyPage(HttpSession session){
        // 세션에서 로그인한 유저 정보 꺼내서 반환
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        User freshUser = userService.getUserByEmail(user.getEmail());
        return ResponseEntity.ok(freshUser);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<?> newNickname(@RequestBody Map<String, String> request, HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String nickname = request.get("nickname");
        userService.updateNickname(user, nickname);
        User updateUser = userService.getUserByEmail(user.getEmail()); // DB에서 최신 정보 꺼내오기
        session.setAttribute("loginUser", updateUser); // 세션 갱신
        return ResponseEntity.ok("닉네임 수정 성공");
    }

    @PatchMapping("/password")
    public ResponseEntity<?> newPassword(@RequestBody Map<String, String> request, HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        String newPasswordConfirm = request.get("newPasswordConfirm");
        userService.updatePassword(user, currentPassword, newPassword, newPasswordConfirm);
        User updateUser = userService.getUserByEmail(user.getEmail()); // DB에서 최신 정보 꺼내오기
        session.setAttribute("loginUser", updateUser); // 세션 갱신
        return ResponseEntity.ok("비밀번호 수정 성공");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> request, HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String password = request.get("password");
        userService.deleteUser(user, password);
        session.invalidate();
        return ResponseEntity.ok("회원탈퇴 성공");
    }

}
