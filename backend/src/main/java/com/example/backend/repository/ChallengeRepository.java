package com.example.backend.repository;

import com.example.backend.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByStatus(String status);  // ONGOING / ENDED 챌린지 조회
    List<Challenge> findByStatusAndEndDateBefore(String status, LocalDateTime dateTime); // 스케줄러용
}
