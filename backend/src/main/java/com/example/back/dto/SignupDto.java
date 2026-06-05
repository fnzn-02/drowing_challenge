package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupDto {
    private String email;
    private String password;
    private String passwordConfirm;
    private String nickname;
}
