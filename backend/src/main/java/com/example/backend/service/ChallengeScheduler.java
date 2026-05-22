package com.example.backend.service;

import com.example.backend.entity.Challenge;
import com.example.backend.entity.Drawing;
import com.example.backend.repository.ChallengeRepository;
import com.example.backend.repository.DrawingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;
    private final DrawingRepository drawingRepository;

    // 매일 자정 00:00 — 종료 시간이 지난 챌린지 자동 처리
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeChallenges() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[스케줄러] 챌린지 종료 처리 시작: {}", now);

        // 종료 시간이 지났지만 아직 ONGOING 상태인 챌린지 조회
        List<Challenge> toClose = challengeRepository.findByStatusAndEndDateBefore("ONGOING", now);

        if (toClose.isEmpty()) {
            log.info("[스케줄러] 종료할 챌린지 없음");
            return;
        }

        for (Challenge challenge : toClose) {
            // 1. 챌린지 상태 → ENDED
            challenge.setStatus("ENDED");
            challengeRepository.save(challenge);

            // 2. 좋아요 순 상위 3개 그림에 메달 부여
            List<Drawing> topDrawings =
                    drawingRepository.findByChallengeIdOrderByLikeCountDesc(challenge.getId());

            String[] medals = {"GOLD", "SILVER", "BRONZE"};
            for (int i = 0; i < Math.min(3, topDrawings.size()); i++) {
                topDrawings.get(i).setMedal(medals[i]);
                drawingRepository.save(topDrawings.get(i));
                log.info("[스케줄러] 챌린지 {} - {} 메달: 그림 ID {}",
                        challenge.getId(), medals[i], topDrawings.get(i).getId());
            }

            log.info("[스케줄러] 챌린지 '{}' 종료 처리 완료", challenge.getTitle());
        }
    }
}
