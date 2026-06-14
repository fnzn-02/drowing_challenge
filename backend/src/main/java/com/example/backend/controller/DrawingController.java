package com.example.backend.controller;

import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.service.DrawingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DrawingController {

    private final DrawingService drawingService;

    // POST /challenges/{id}/draw — 그림 제출 (multipart/form-data)
    @PostMapping("/challenges/{id}/draw")
    public ResponseEntity<?> submitDrawing(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "comment", required = false) String comment,
            HttpSession session) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        drawingService.submitDrawing(id, loginUser, imageFile, comment);
        return ResponseEntity.ok("그림 제출 성공");
    }

    // GET /challenges/{id}/drawings — 챌린지별 그림 목록 (좋아요 순)
    @GetMapping("/challenges/{id}/drawings")
    public ResponseEntity<List<Drawing>> getDrawingsByChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(drawingService.getDrawingsByChallenge(id));
    }

    // GET /drawings/{id} — 그림 단건 상세 조회
    @GetMapping("/drawings/{id}")
    public ResponseEntity<Drawing> getDrawing(@PathVariable Long id) {
        return ResponseEntity.ok(drawingService.getDrawingById(id));
    }

    // DELETE /drawings/{id} — 그림 삭제 (본인만)
    @DeleteMapping("/drawings/{id}")
    public ResponseEntity<?> deleteDrawing(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        drawingService.deleteDrawing(id, loginUser);
        return ResponseEntity.ok("그림 삭제 성공");
    }
}
