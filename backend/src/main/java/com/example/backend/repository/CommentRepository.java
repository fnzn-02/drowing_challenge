package com.example.backend.repository;

import com.example.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDrawingIdOrderByCreatedAtAsc(Long drawingId); // 그림의 댓글 목록 (오래된 순)
}
