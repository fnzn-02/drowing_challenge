package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🌟 오류 해결 포인트: 람다식 내부에서 아무것도 건드리지 않는 c -> {} 형태로 적어주면
                // 이미 만들어두신 WebConfig의 CORS 설정을 스프링 시큐리티가 기본값으로 그대로 가져다 씁니다!
                .cors(cors -> {})

                // CSRF 및 로그인창 프리패스 설정
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}