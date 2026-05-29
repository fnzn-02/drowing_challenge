package com.example.backend.repository;

import com.example.backend.entity.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DrawingRepository extends JpaRepository<Drawing, Long> {
    List<Drawing> findByChallengeIdOrderByLikeCountDesc(Long challengeId); // 랭킹용
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);    // 중복 제출 방지
    List<Drawing> findByUserId(Long userId);                                 // 마이페이지용
}
