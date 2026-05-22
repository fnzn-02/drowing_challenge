package com.example.backend.controller;

import com.example.backend.entity.Challenge;
import com.example.backend.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    // GET /challenges — 진행 중인 챌린지 목록
    @GetMapping
    public ResponseEntity<List<Challenge>> getOngoing() {
        return ResponseEntity.ok(challengeService.getOngoingChallenges());
    }

    // GET /challenges/ended — 종료된 챌린지 목록
    @GetMapping("/ended")
    public ResponseEntity<List<Challenge>> getEnded() {
        return ResponseEntity.ok(challengeService.getEndedChallenges());
    }

    // GET /challenges/{id} — 챌린지 상세
    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }
}
