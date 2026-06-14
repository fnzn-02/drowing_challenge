package com.example.backend.service;

import com.example.backend.dto.CommentResponseDto;
import com.example.backend.entity.Comment;
import com.example.backend.entity.Drawing;
import com.example.backend.entity.User;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.DrawingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DrawingRepository drawingRepository;

    // 댓글 작성
    public CommentResponseDto addComment(Long drawingId, User user, String content) {
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new IllegalArgumentException("그림을 찾을 수 없습니다."));

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        Comment comment = new Comment(null, drawing, user, content.trim(), null);
        Comment saved = commentRepository.save(comment);
        return new CommentResponseDto(saved);
    }

    // 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long drawingId) {
        return commentRepository.findByDrawingIdOrderByCreatedAtAsc(drawingId)
                .stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 댓글 삭제 (본인만 가능)
    public void deleteComment(Long commentId, User loginUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
