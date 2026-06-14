package com.example.backend.controller;

import com.example.backend.dto.DrawingResponseDto; // [추가됨] 프론트엔드 API 규격을 맞추기 위해 DTO import
import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.service.DrawingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Drawing(그림) 관련 HTTP 요청을 받아 처리하는 컨트롤러 레이어.
 *
 * 프론트엔드(React)에서 오는 요청을 받아 서비스로 넘기고,
 * 서비스가 처리한 결과를 JSON 형태로 다시 프론트엔드로 응답한다.
 *
 * @Slf4j: Lombok이 제공하는 로깅 어노테이션. 백엔드-프론트 통신 시 데이터를 로그로 기록.
 * @RestController: @Controller + @ResponseBody. 메서드 반환값을 JSON으로 자동 직렬화.
 * @RequiredArgsConstructor: final 필드(DrawingService)를 생성자로 자동 주입.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DrawingController {

    private final DrawingService drawingService;

    /**
     * 그림 제출 API.
     * POST /challenges/{id}/draw
     *
     * 로그인한 유저가 특정 챌린지에 그림을 제출한다.
     * multipart/form-data 형식으로 이미지 파일과 코멘트를 받는다.
     * 세션에서 로그인 유저를 꺼내 인증을 확인한다.
     *
     * @param id        URL 경로의 챌린지 ID
     * @param imageFile 업로드할 이미지 파일
     * @param comment   그림 코멘트 (선택)
     * @param session   로그인 세션
     */
    @PostMapping("/challenges/{id}/draw")
    public ResponseEntity<?> submitDrawing(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "comment", required = false) String comment,
            HttpSession session) throws IOException {

        log.info("[DrawingController] 그림 제출 요청 수신 - challengeId: {}", id);

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.warn("[DrawingController] 미인증 그림 제출 시도 - challengeId: {}", id);
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        drawingService.submitDrawing(id, loginUser, imageFile, comment);

        log.info("[DrawingController] 그림 제출 응답 완료 - challengeId: {}, userId: {}", id, loginUser.getId());
        return ResponseEntity.ok("그림 제출 성공");
    }

    /**
     * [추가됨] 챌린지별 그림 목록 조회 API.
     * GET /challenges/{id}/drawings
     *
     * 특정 챌린지에 제출된 그림 목록을 좋아요 수 내림차순으로 반환한다.
     * 반환 형식은 프론트엔드의 interface Drowing 규격(DrawingResponseDto)인 중첩 객체 구조를 따른다.
     *
     * 반환 JSON 예시:
     * [
     * {
     * "id": 1, "comment": "멋진 그림!", "imagePath": "/uploads/...", "likeCount": 5, "medal": "GOLD",
     * "user": { "id": 12, "nickname": "그림천재", "email": "test@test.com" },
     * "challenge": { "id": 3, "title": "동물 그리기 챌린지" }
     * },
     * ...
     * ]
     *
     * @param id URL 경로의 챌린지 ID (challengeId)
     * @return 프론트 규격에 맞는 DrawingResponseDto 객체 배열 (JSON)
     */
    @GetMapping("/challenges/{id}/drawings")
    public ResponseEntity<List<DrawingResponseDto>> getDrawingsByChallenge(@PathVariable Long id) {
        log.info("[DrawingController] 챌린지별 그림 목록 조회 요청 수신 - challengeId: {}", id);

        List<DrawingResponseDto> result = drawingService.getDrawingsByChallenge(id);

        log.info("[DrawingController] 챌린지별 그림 목록 응답 전송 - challengeId: {}, 응답 객체 배열: {}", id, result);
        return ResponseEntity.ok(result);
    }

    /**
     * [추가됨] drawing_id + user_id 복합 조건으로 그림 조회 API.
     * GET /drawings/search?drawingId={drawingId}&userId={userId}
     *
     * 프론트엔드가 특정 그림 ID와 유저 ID를 함께 보내면,
     * 해당 조건에 맞는 그림을 찾아 계층형 데이터 구조의 DrawingResponseDto 객체 배열로 반환한다.
     *
     * 처리 흐름:
     * 1. 프론트에서 drawingId + userId를 쿼리 파라미터로 전송
     * 2. 컨트롤러가 파라미터를 받아 서비스로 전달
     * 3. 서비스가 drawing_id + user_id 조건으로 DB 조회 후 중첩 구조의 DTO 객체 배열 생성
     * 4. 컨트롤러가 결과를 로그로 출력하고 프론트로 JSON 응답
     *
     * 반환 JSON 예시 (찾은 경우):
     * [{ "id": 1, "comment": "...", "imagePath": "/uploads/...", "likeCount": 3, "medal": null,
     * "user": { "id": 5, "nickname": "홍길동", "email": "hong@test.com" },
     * "challenge": { "id": 2, "title": "풍경화 챌린지" } }]
     *
     * 반환 JSON 예시 (못 찾은 경우): []
     */
    @GetMapping("/drawings/search")
    public ResponseEntity<List<DrawingResponseDto>> getDrawingByDrawingIdAndUserId(
            @RequestParam Long drawingId,
            @RequestParam Long userId) {

        log.info("[DrawingController] drawing_id + user_id 조회 요청 수신 - drawingId: {}, userId: {}", drawingId, userId);

        List<DrawingResponseDto> result = drawingService.getDrawingsByDrawingIdAndUserId(drawingId, userId);

        // 객체 배열 로그 출력 (팀원 요청사항: 컨트롤러에서 객체 로그 찍기)
        log.info("[DrawingController] drawing_id + user_id 조회 응답 객체 배열 로그: {}", result);
        log.info("[DrawingController] drawing_id + user_id 조회 응답 전송 - 결과 개수: {}", result.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 그림 단건 상세 조회 API.
     * GET /drawings/{id}
     *
     * 그림 ID로 특정 그림 하나의 상세 정보를 조회한다.
     *
     * @param id 조회할 그림의 ID
     * @return 조회된 Drawing 엔티티 (JSON)
     */
    @GetMapping("/drawings/{id}")
    public ResponseEntity<Drawing> getDrawing(@PathVariable Long id) {
        log.info("[DrawingController] 그림 단건 조회 요청 수신 - drawingId: {}", id);
        Drawing result = drawingService.getDrawingById(id);
        log.info("[DrawingController] 그림 단건 조회 응답 전송 - drawingId: {}, imagePath: {}", id, result.getImagePath());
        return ResponseEntity.ok(result);
    }

    /**
     * 그림 삭제 API.
     * DELETE /drawings/{id}
     *
     * 로그인한 유저가 본인이 제출한 그림을 삭제한다.
     * 세션에서 로그인 유저를 꺼내 인증 및 본인 확인을 한다.
     *
     * @param id      삭제할 그림의 ID
     * @param session 로그인 세션
     */
    @DeleteMapping("/drawings/{id}")
    public ResponseEntity<?> deleteDrawing(@PathVariable Long id, HttpSession session) {
        log.info("[DrawingController] 그림 삭제 요청 수신 - drawingId: {}", id);

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.warn("[DrawingController] 미인증 그림 삭제 시도 - drawingId: {}", id);
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        drawingService.deleteDrawing(id, loginUser);

        log.info("[DrawingController] 그림 삭제 응답 완료 - drawingId: {}, userId: {}", id, loginUser.getId());
        return ResponseEntity.ok("그림 삭제 성공");
    }
}
