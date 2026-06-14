package com.example.backend.controller;

import com.example.backend.dto.CommentResponseDto;
import com.example.backend.entity.User;
import com.example.backend.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // POST /drawings/{id}/comments — 댓글 작성
    @PostMapping("/drawings/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        CommentResponseDto response = commentService.addComment(id, loginUser, body.get("content"));
        return ResponseEntity.ok(response);
    }

    // GET /drawings/{id}/comments — 댓글 목록 조회
    @GetMapping("/drawings/{id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComments(id));
    }

    // DELETE /comments/{id} — 댓글 삭제 (본인만)
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        commentService.deleteComment(id, loginUser);
        return ResponseEntity.ok("댓글 삭제 성공");
    }
}
