package com.example.backend.service;

import com.example.backend.entity.Challenge;
import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.repository.ChallengeRepository;
import com.example.backend.repository.DrawingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DrawingService {

    private final DrawingRepository drawingRepository;
    private final ChallengeRepository challengeRepository;

    // 그림 제출
    public Drawing submitDrawing(Long challengeId, User user,
                                 MultipartFile imageFile, String comment) throws IOException {
        // 1. 챌린지 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 2. 진행 중인 챌린지인지 확인
        if (!"ONGOING".equals(challenge.getStatus())) {
            throw new IllegalArgumentException("종료된 챌린지입니다.");
        }

        // 3. 이미 제출했는지 확인 (챌린지당 1회)
        if (drawingRepository.existsByChallengeIdAndUserId(challengeId, user.getId())) {
            throw new IllegalArgumentException("이미 그림을 제출했습니다.");
        }

        // 4. 이미지 파일 저장 (/uploads 폴더)
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageFile.getBytes());

        // 5. DB 저장
        Drawing drawing = new Drawing(null, challenge, user,
                "/uploads/" + fileName, comment, 0, null, null);
        return drawingRepository.save(drawing);
    }

    // 그림 단건 조회
    public Drawing getDrawingById(Long id) {
        return drawingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));
    }

    // 챌린지별 그림 목록 (좋아요 순)
    public List<Drawing> getDrawingsByChallenge(Long challengeId) {
        return drawingRepository.findByChallengeIdOrderByLikeCountDesc(challengeId);
    }

    // 그림 삭제 (본인만 가능)
    public void deleteDrawing(Long drawingId, User loginUser) {
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));

        if (!drawing.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("본인 그림만 삭제할 수 있습니다.");
        }

        drawingRepository.delete(drawing);
    }

    // 마이페이지: 내가 제출한 그림 목록
    public List<Drawing> getDrawingsByUser(Long userId) {
        return drawingRepository.findByUserId(userId);
    }
}
