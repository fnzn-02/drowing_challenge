package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Drawing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String imagePath;

    @Column(length = 200)
    private String comment;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likeCount;

    @Column(length = 10)
    private String medal; // GOLD / SILVER / BRONZE / null

    @OneToMany(mappedBy = "drawing", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
