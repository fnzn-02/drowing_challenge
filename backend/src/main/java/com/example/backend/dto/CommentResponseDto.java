package com.example.backend.dto;

import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private final Long id;
    private final String content;
    private UserInfoDto user;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();

        if (comment.getUser() != null) {
            this.user = new UserInfoDto(comment.getUser());
        }
    }

    @Getter
    public static class UserInfoDto {
        private final Long id;
        private final String nickname;
        private final String email;

        public UserInfoDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
        }
    }

    @Override
    public String toString() {
        return "CommentResponseDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + (user != null ? "{id=" + user.getId() + ", nickname='" + user.getNickname() + "'}" : "null") +
                '}';
    }
}