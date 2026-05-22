package com.example.backend.service;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.SignupDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void signup(SignupDto signupDto) {
        if (!emailService.isVerified(signupDto.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        if (userRepository.findByEmail(signupDto.getEmail()) != null) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        if (userRepository.findByNickname(signupDto.getNickname()) != null) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }
        validatePassword(signupDto.getPassword());
        if (!signupDto.getPassword().equals(signupDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());
        userRepository.save(new User(null, signupDto.getEmail(), encodedPassword, signupDto.getNickname(), null));
    }

    public User login(LoginDto loginDto) {
        User loginUser = userRepository.findByEmail(loginDto.getEmail());
        if (loginUser == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        return loginUser;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateNickname(User user, String nickname) {
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public void updatePassword(User user, String currentPassword, String newPassword, String newPasswordConfirm) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 틀렸습니다.");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        userRepository.delete(user);
    }

    // 비밀번호 유효성 검사 (8자 이상, 특수문자 포함) — signup/updatePassword 공통 사용
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new IllegalArgumentException("특수문자를 포함해야 합니다.");
        }
    }
}
