-- 테스트용 챌린지 데이터 삽입 (중복 방지를 위해 id 지정 및 IGNORE 사용)
INSERT IGNORE INTO challenge (id, title, description, start_date, end_date, status, created_at)
VALUES (1, '고양이 그리기', '귀여운 고양이를 그려보세요!', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'ONGOING', NOW());
