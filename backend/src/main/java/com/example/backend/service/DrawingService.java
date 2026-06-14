package com.example.backend.service;

import com.example.backend.dto.DrawingResponseDto; // [추가됨] 프론트엔드 API 규격을 맞추기 위해 DTO import
import com.example.backend.entity.Challenge;
import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.repository.ChallengeRepository;
import com.example.backend.repository.DrawingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // [추가됨] DTO 리스트 매핑을 위해 import

/**
 * Drawing(그림) 관련 비즈니스 로직을 담당하는 서비스 레이어.
 *
 * 컨트롤러(Controller)와 데이터베이스(Repository) 사이에 위치하며,
 * 실제 비즈니스 규칙(제출 가능 여부 검증, 파일 저장, 메달 여부 등)을 처리한다.
 *
 * @Slf4j: Lombok이 제공하는 로깅 어노테이션. log.info(), log.warn(), log.error() 등 사용 가능.
 * @Service: 스프링이 이 클래스를 서비스 빈(Bean)으로 등록하도록 지시.
 * @RequiredArgsConstructor: final 필드를 매개변수로 받는 생성자를 자동 생성 → 의존성 주입(DI) 처리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DrawingService {

    private final DrawingRepository drawingRepository;
    private final ChallengeRepository challengeRepository;

    /**
     * 그림 제출 처리.
     *
     * 처리 흐름:
     * 1. challengeId로 챌린지 존재 여부 확인 (없으면 예외)
     * 2. 해당 챌린지가 현재 진행 중(ONGOING)인지 확인 (종료된 챌린지에는 제출 불가)
     * 3. 동일 유저가 같은 챌린지에 이미 제출했는지 확인 (챌린지당 1회 제출 제한)
     * 4. 서버의 /uploads/ 폴더에 이미지 파일 저장 (파일명은 UUID로 중복 방지)
     * 5. Drawing 엔티티를 생성해 DB에 저장
     *
     * @param challengeId 제출할 챌린지의 ID
     * @param user        제출하는 로그인 유저 객체
     * @param imageFile   업로드할 이미지 파일 (multipart/form-data)
     * @param comment     그림에 남길 한마디 코멘트 (선택사항)
     * @return 저장된 Drawing 엔티티
     * @throws IOException 이미지 파일 저장 중 오류 발생 시
     */
    public Drawing submitDrawing(Long challengeId, User user,
                                 MultipartFile imageFile, String comment) throws IOException {
        log.info("[DrawingService] 그림 제출 요청 - challengeId: {}, userId: {}", challengeId, user.getId());

        // 1. 챌린지 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 2. 진행 중인 챌린지인지 확인
        if (!"ONGOING".equals(challenge.getStatus())) {
            log.warn("[DrawingService] 종료된 챌린지에 제출 시도 - challengeId: {}", challengeId);
            throw new IllegalArgumentException("종료된 챌린지입니다.");
        }

        // 3. 이미 제출했는지 확인 (챌린지당 1회)
        if (drawingRepository.existsByChallengeIdAndUserId(challengeId, user.getId())) {
            log.warn("[DrawingService] 중복 제출 시도 - challengeId: {}, userId: {}", challengeId, user.getId());
            throw new IllegalArgumentException("이미 그림을 제출했습니다.");
        }

        // 4. 이미지 파일 저장 (/uploads 폴더)
        // UUID로 파일명을 무작위화하여 동일한 파일명 충돌 방지
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent()); // 폴더가 없으면 생성
        Files.write(filePath, imageFile.getBytes());   // 파일 바이트 기록

        // 5. DB 저장
        // medal은 챌린지 종료 시 스케줄러가 자동으로 부여하므로 초기값은 null
        Drawing drawing = new Drawing(null, challenge, user,
                "/uploads/" + fileName, comment, 0, null, null);
        Drawing saved = drawingRepository.save(drawing);

        log.info("[DrawingService] 그림 제출 완료 - drawingId: {}, imagePath: {}", saved.getId(), saved.getImagePath());
        return saved;
    }

    /**
     * 그림 단건 조회.
     *
     * drawing의 PK(id)로 특정 그림 하나를 조회한다.
     * 해당 ID의 그림이 없으면 예외를 던진다.
     *
     * @param id 조회할 그림의 ID
     * @return 조회된 Drawing 엔티티
     */
    public Drawing getDrawingById(Long id) {
        log.info("[DrawingService] 그림 단건 조회 - drawingId: {}", id);
        return drawingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));
    }

    /**
     * [추가됨] 챌린지별 그림 목록 조회 후 프론트 규격 DTO로 변환해서 반환.
     *
     * Drawing 엔티티를 직접 반환하면 challenge/user 객체 전체가 JSON에 포함되어
     * 프론트의 interface Drowing 규격과 맞지 않는다.
     * DrawingResponseDto로 변환하면 challengeId(숫자), userId(숫자) 형태로
     * 프론트 규격에 딱 맞게 내려간다.
     *
     * @param challengeId 조회할 챌린지의 ID
     * @return 프론트 규격(DrawingResponseDto) 객체 배열
     */
    public List<DrawingResponseDto> getDrawingsByChallenge(Long challengeId) {
        log.info("[DrawingService] 챌린지별 그림 목록 조회 요청 - challengeId: {}", challengeId);

        List<DrawingResponseDto> result = drawingRepository.findByChallengeIdOrderByLikeCountDesc(challengeId)
                .stream()
                .map(DrawingResponseDto::new)   // Drawing 엔티티 → DrawingResponseDto 변환
                .collect(Collectors.toList());

        log.info("[DrawingService] 챌린지별 그림 목록 조회 완료 - challengeId: {}, 조회된 개수: {}", challengeId, result.size());
        return result;
    }

    /**
     * [추가됨] drawing_id + user_id 복합 조건으로 그림 조회 후 DTO 객체 배열 반환.
     *
     * 프론트엔드가 특정 그림 ID와 유저 ID를 함께 보내왔을 때,
     * 해당 조건에 맞는 그림을 찾아 DrawingResponseDto 리스트로 반환한다.
     * 결과가 0개(해당 유저의 그림이 없음) 또는 1개(찾음)인 배열이 반환된다.
     *
     * @param drawingId 조회할 그림 ID (프론트에서 보낸 drawing_id)
     * @param userId    조회할 유저 ID (프론트에서 보낸 user_id)
     * @return 조건에 맞는 DrawingResponseDto 객체 배열 (0개 또는 1개)
     */
    public List<DrawingResponseDto> getDrawingsByDrawingIdAndUserId(Long drawingId, Long userId) {
        log.info("[DrawingService] drawing_id + user_id 조회 요청 - drawingId: {}, userId: {}", drawingId, userId);

        List<DrawingResponseDto> result = drawingRepository.findByIdAndUserId(drawingId, userId)
                .map(DrawingResponseDto::new)    // Optional<Drawing> → Optional<DrawingResponseDto>
                .map(List::of)                   // Optional<DTO> → Optional<List<DTO>> (1개짜리 리스트)
                .orElse(List.of());              // 없으면 빈 리스트 반환

        log.info("[DrawingService] drawing_id + user_id 조회 완료 - 결과: {}", result);
        return result;
    }

    /**
     * 그림 삭제 (본인만 가능).
     *
     * 삭제 요청자와 그림의 업로더가 동일한지 확인 후 삭제한다.
     * 본인의 그림이 아니면 예외를 던진다.
     *
     * @param drawingId  삭제할 그림의 ID
     * @param loginUser  현재 로그인된 유저 (세션에서 가져옴)
     */
    public void deleteDrawing(Long drawingId, User loginUser) {
        log.info("[DrawingService] 그림 삭제 요청 - drawingId: {}, requestUserId: {}", drawingId, loginUser.getId());

        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));

        if (!drawing.getUser().getId().equals(loginUser.getId())) {
            log.warn("[DrawingService] 권한 없는 삭제 시도 - drawingId: {}, ownerId: {}, requestUserId: {}",
                    drawingId, drawing.getUser().getId(), loginUser.getId());
            throw new IllegalArgumentException("본인 그림만 삭제할 수 있습니다.");
        }

        drawingRepository.delete(drawing);
        log.info("[DrawingService] 그림 삭제 완료 - drawingId: {}", drawingId);
    }

    /**
     * 마이페이지: 내가 제출한 그림 목록 조회.
     *
     * 로그인한 유저의 ID로 그 유저가 지금까지 제출한 모든 그림 목록을 조회한다.
     * 마이페이지에서 "내 그림 보기" 기능에 사용된다.
     *
     * @param userId 조회할 유저의 ID
     * @return 해당 유저가 제출한 Drawing 엔티티 목록
     */
    public List<Drawing> getDrawingsByUser(Long userId) {
        log.info("[DrawingService] 마이페이지 그림 목록 조회 - userId: {}", userId);
        return drawingRepository.findByUserId(userId);
    }
}
