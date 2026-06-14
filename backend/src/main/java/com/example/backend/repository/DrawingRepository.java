package com.example.backend.repository;

import com.example.backend.entity.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Drawing(그림) 테이블에 대한 데이터 접근 레이어(Repository).
 * Spring Data JPA가 인터페이스만 보고 구현체를 자동으로 생성해준다.
 * JpaRepository<Drawing, Long>을 상속받으면 save/findById/findAll/delete 등
 * 기본 CRUD 메서드를 따로 구현 없이 사용할 수 있다.
 */
public interface DrawingRepository extends JpaRepository<Drawing, Long> {

    /**
     * 챌린지 ID로 해당 챌린지에 제출된 그림 목록을 좋아요 수 내림차순으로 조회.
     * 챌린지 결과 랭킹 화면에서 사용.
     * @param challengeId 조회할 챌린지 ID
     * @return 좋아요 많은 순으로 정렬된 그림 목록
     */
    List<Drawing> findByChallengeIdOrderByLikeCountDesc(Long challengeId);

    /**
     * 특정 챌린지에 특정 유저가 이미 그림을 제출했는지 여부 확인.
     * 챌린지당 1인 1회 제출 제한을 위한 중복 검사에 사용.
     * @param challengeId 챌린지 ID
     * @param userId      유저 ID
     * @return 이미 제출했으면 true, 아직 안 했으면 false
     */
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);

    /**
     * 유저 ID로 해당 유저가 제출한 모든 그림 목록 조회.
     * 마이페이지에서 내 그림 모아보기 기능에 사용.
     * @param userId 유저 ID
     * @return 해당 유저가 제출한 그림 목록
     */
    List<Drawing> findByUserId(Long userId);

    /**
     * [추가됨] 그림 ID + 유저 ID 복합 조건으로 그림 단건 조회.
     * "특정 그림을 특정 유저가 제출한 것이 맞는지" 확인하거나,
     * 프론트엔드에서 drawing_id + user_id를 보내왔을 때 해당 데이터를 찾기 위해 사용.
     * @param id     그림 ID (drawing_id)
     * @param userId 유저 ID (user_id)
     * @return 조건에 맞는 그림 (없으면 Optional.empty())
     */
    Optional<Drawing> findByIdAndUserId(Long id, Long userId);

    /**
     * [추가됨] 챌린지 ID로 해당 챌린지에 제출된 그림 목록을 좋아요 수 내림차순으로 조회 (Fetch Join 적용).
     * DTO 변환 시 발생할 수 있는 N+1 무한 쿼리 문제를 방지하기 위해
     * 그림 엔티티를 조회할 때 연관된 유저(User)와 챌린지(Challenge) 정보를 한방에 묶어서 긁어온다.
     * @param challengeId 조회할 챌린지 ID
     * @return 유저 및 챌린지 정보가 함께 로딩되고, 좋아요 많은 순으로 정렬된 그림 목록
     */
    @Query("select d from Drawing d " +
            "join fetch d.user " +
            "join fetch d.challenge " +
            "where d.challenge.id = :challengeId " +
            "order by d.likeCount desc")
    List<Drawing> findByChallengeIdWithUserAndChallenge(@Param("challengeId") Long challengeId);
}

