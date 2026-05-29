package com.example.backend.service;

import com.example.backend.entity.Drawing;
import com.example.backend.entity.Like;
import com.example.backend.entity.User;
import com.example.backend.repository.DrawingRepository;
import com.example.backend.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final DrawingRepository drawingRepository;

    // 좋아요 토글 (추가 or 취소)
    @Transactional
    public String toggleLike(Long drawingId, User user) {
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));

        Optional<Like> existing = likeRepository.findByDrawingIdAndUserId(drawingId, user.getId());

        if (existing.isPresent()) {
            // 이미 좋아요 → 취소
            likeRepository.delete(existing.get());
            drawing.setLikeCount(drawing.getLikeCount() - 1);
            drawingRepository.save(drawing);
            return "좋아요 취소";
        } else {
            // 좋아요 추가
            likeRepository.save(new Like(null, drawing, user, null));
            drawing.setLikeCount(drawing.getLikeCount() + 1);
            drawingRepository.save(drawing);
            return "좋아요 추가";
        }
    }

    // 특정 그림에 로그인 유저가 좋아요 눌렀는지 여부 반환
    public boolean isLiked(Long drawingId, Long userId) {
        return likeRepository.existsByDrawingIdAndUserId(drawingId, userId);
    }
}
