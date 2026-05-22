package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;          // 챌린지 제목

    @Column(columnDefinition = "TEXT")
    private String description;    // 챌린지 설명

    @Column(nullable = false)
    private LocalDateTime startDate;  // 시작일

    @Column(nullable = false)
    private LocalDateTime endDate;    // 종료일

    @Column(nullable = false, length = 20)
    private String status;         // ONGOING / ENDED

    @CreationTimestamp
    private LocalDateTime createdAt;
}
