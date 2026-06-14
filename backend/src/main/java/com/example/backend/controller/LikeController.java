package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // POST /drawings/{id}/likes — 좋아요 토글 (추가 or 취소)
    @PostMapping("/drawings/{id}/likes")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        String result = likeService.toggleLike(id, loginUser);
        return ResponseEntity.ok(Map.of("message", result));
    }

    // GET /drawings/{id}/likes/status — 현재 유저의 좋아요 여부 확인
    @GetMapping("/drawings/{id}/likes/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.ok(Map.of("liked", false));
        }

        boolean liked = likeService.isLiked(id, loginUser.getId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
