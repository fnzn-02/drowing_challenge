package com.example.backend.repository;

import com.example.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByDrawingIdAndUserId(Long drawingId, Long userId); // 좋아요 여부 확인
    boolean existsByDrawingIdAndUserId(Long drawingId, Long userId);       // 좋아요 존재 여부
}
