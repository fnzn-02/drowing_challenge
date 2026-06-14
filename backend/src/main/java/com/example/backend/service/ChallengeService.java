package com.example.backend.service;

import com.example.backend.entity.Challenge;
import com.example.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    // 진행 중인 챌린지 목록
    public List<Challenge> getOngoingChallenges() {
        return challengeRepository.findByStatus("ONGOING");
    }

    // 종료된 챌린지 목록
    public List<Challenge> getEndedChallenges() {
        return challengeRepository.findByStatus("ENDED");
    }

    // 챌린지 단건 조회
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));
    }
}
