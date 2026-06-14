package com.example.backend.dto;

import com.example.backend.entity.Challenge;
import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import lombok.Getter;

/**
 * [추가됨] Drawing 엔티티를 프론트엔드 규격에 맞게 변환하는 DTO (Data Transfer Object).
 *
 * ★ DTO를 별도로 만드는 이유:
 *   Drawing 엔티티는 DB 관계 매핑 때문에 challenge(객체 전체), user(객체 전체)를
 *   그대로 들고 있다. 이 엔티티를 직접 JSON으로 반환하면:
 *     - 프론트가 원하지 않는 민감 정보(이메일 등)가 노출될 수 있음
 *     - challenge/user가 중첩된 복잡한 JSON 구조가 되어 프론트 interface 규격과 불일치
 *     - JPA Lazy Loading 세션 문제로 직렬화 에러(LazyInitializationException) 가능성
 *
 * ★ 이 DTO의 역할:
 *   Drawing 엔티티에서 프론트가 필요한 값만 꺼내 평평한(Flat) 구조로 만들어준다.
 *   challengeId(Long), userId(Long) 처럼 ID 값만 뽑아 프론트 interface와 정확히 맞춘다.
 *
 * ★ 프론트 TypeScript interface (ViewDrowing.tsx):
 *   interface Drowing {
 *     id: number;          → Drawing.id
 *     comment: string;     → Drawing.comment
 *     imagePath: string;   → Drawing.imagePath
 *     likeCount: number;   → Drawing.likeCount
 *     medal: string;       → Drawing.medal
 *     challengeId: number; → Drawing.challenge.getId() [핵심 변환]
 *     userId: number;      → Drawing.user.getId()      [핵심 변환]
 *   }
 *
 * @Getter: Lombok이 모든 필드의 getter를 자동으로 생성.
 *          Jackson(JSON 변환 라이브러리)이 getter를 이용해 JSON을 만든다.
 */
@Getter
public class DrawingResponseDto {

    /** 그림의 고유 ID (DB auto increment PK) */
    private final Long id;

    /** 그림 제출 시 작성한 코멘트 (없으면 null) */
    private final String comment;

    /** 서버에 저장된 이미지 파일 경로 (예: /uploads/uuid_파일명.png) */
    private final String imagePath;

    /** 좋아요 수 (초기값 0, 좋아요 토글 시 증감) */
    private final int likeCount;

    /** 메달 등급 (GOLD / SILVER / BRONZE / null). 챌린지 종료 시 스케줄러가 부여. */
    private final String medal;

    /** 프론트로 넘길 중첩 객체들 */
    private UserInfoDto user;
    private final int commentCount;
    private ChallengeInfoDto challenge;

    /**
     * Drawing 엔티티를 받아 DTO로 변환하는 생성자.
     *
     * 서비스 레이어에서 Drawing 엔티티 목록을 stream().map(DrawingResponseDto::new)로 변환할 때 사용.
     *
     * @param drawing 변환할 Drawing 엔티티
     */
    public DrawingResponseDto(Drawing drawing) {
        this.id = drawing.getId();
        this.comment = drawing.getComment();
        this.imagePath = drawing.getImagePath();
        this.likeCount = drawing.getLikeCount();
        this.medal = drawing.getMedal();
        this.commentCount = drawing.getComments() != null ? drawing.getComments().size() : 0;

        // ORM 객체 그래프 탐색을 통해 한방에 조립
        if (drawing.getUser() != null) {
            this.user = new UserInfoDto(drawing.getUser());
        }
        if (drawing.getChallenge() != null) {
            this.challenge = new ChallengeInfoDto(drawing.getChallenge());
        }

    }

    /** 중첩객체를 static으로 설정 */
    @Getter
    public static class UserInfoDto {
        private final Long id;
        private final String nickname;

        public UserInfoDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
        }
    }
    @Getter
    public static class ChallengeInfoDto {
        private final Long id;
        private final String title;

        public ChallengeInfoDto(Challenge challenge) {
            this.id = challenge.getId();
            this.title = challenge.getTitle();
        }
    }

    /**
     * 로깅 시 객체 내용을 명확하게 확인하기 위한 toString 오버라이드.
     * 컨트롤러/서비스에서 log.info("결과: {}", result) 로그를 찍을 때
     * DrawingResponseDto{id=1, userId=5, ...} 형태로 출력된다.
     */
    @Override
    public String toString() {
        return "DrawingResponseDto{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", likeCount=" + likeCount +
                ", medal='" + medal + '\'' +
                ", user=" + (user != null ? "{id=" + user.getId() + ", nickname='" + user.getNickname() + "'}" : "null") +
                ", challenge=" + (challenge != null ? "{id=" + challenge.getId() + ", title='" + challenge.getTitle() + "'}" : "null") +
                '}';
    }
}
