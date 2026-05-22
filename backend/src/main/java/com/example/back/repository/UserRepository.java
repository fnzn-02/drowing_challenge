package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 유저 찾기
    User findByEmail(String email);
    // 닉네임으로 유저 찾기
    User findByNickname(String nickName);
}
