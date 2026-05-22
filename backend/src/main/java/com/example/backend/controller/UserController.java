package com.example.backend.controller;

import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.service.DrawingService;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final DrawingService drawingService;

    // GET /mypage — 내 정보 + 제출 그림 목록 + 참여한 챌린지 목록
    @GetMapping
    public ResponseEntity<?> getMyPage(HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 최신 유저 정보 조회
        User freshUser = userService.getUserByEmail(user.getEmail());

        // 내가 제출한 그림 목록
        List<Drawing> myDrawings = drawingService.getDrawingsByUser(freshUser.getId());

        // 응답 데이터 조합
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", freshUser);
        response.put("drawings", myDrawings);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<?> newNickname(@RequestBody Map<String, String> request, HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String nickname = request.get("nickname");
        userService.updateNickname(user, nickname);
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
