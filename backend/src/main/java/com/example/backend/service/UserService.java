package com.example.backend.service;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.SignupDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입해주는 Lombok 어노테이션
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void signup(SignupDto signupDto){

        // 이메일 인증 여부 확인
        if(!emailService.isVerified(signupDto.getEmail())){
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        // 이메일 중복 확인
        User userEmail = userRepository.findByEmail(signupDto.getEmail());
        if (userEmail != null){
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // 닉네임 중복 확인
        User userNickName = userRepository.findByNickname(signupDto.getNickname());
        if(userNickName != null){
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        if(signupDto.getPassword().length() < 8){
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        if(!signupDto.getPassword().matches(".*[!@#$%^&*].*")){
            throw new IllegalArgumentException("특수문자를 포함해야 합니다.");
        }

        // 비밀번호 확인
        if(!signupDto.getPassword().equals(signupDto.getPasswordConfirm())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());

        // DB 저장
        User user = new User(null, signupDto.getEmail(), encodedPassword, signupDto.getNickname(), null);
        userRepository.save(user);
    }

    public User login(LoginDto loginDto){

        User loginUser = userRepository.findByEmail(loginDto.getEmail());

        // 이메일 있는지 확인
        if(loginUser == null){
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
        // 비밀번호 맞는지 확인
        if(!passwordEncoder.matches(loginDto.getPassword(), loginUser.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return loginUser;
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateNickname(User user, String nickname){
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public void updatePassword(User user, String currentPassword, String newPassword, String newPasswordConfirm){
        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 틀렸습니다.");
        }

        // 새 비밀번호 확인
        if(!newPassword.equals(newPasswordConfirm)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(newPassword.length() < 8){
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        if(!newPassword.matches(".*[!@#$%^&*].*")){
            throw new IllegalArgumentException("특수문자를 포함해야 합니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(User user, String password){
        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        // 유저 삭제
        userRepository.delete(user);
    }
}
