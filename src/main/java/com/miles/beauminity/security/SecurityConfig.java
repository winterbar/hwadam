package com.miles.beauminity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 필터 설정. 필터를 통과하지 못한 경우 로그인 처리

@Configuration // 환경설정 파일으로 스프링이 가동될 때 객체를 만듦
@EnableWebSecurity // 스프링 시큐리티 사용
public class SecurityConfig {

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 스프링에서 제공하는 암호화
    }
    
    // 스프링 필터 객체
    @Bean // 설정된 메서드로 객체 생성
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        /* CSRF 설정 */
        http.csrf(csrf -> csrf.disable())
            // 인증과 인가 설정
            .authorizeHttpRequests(auth ->
                auth
                .requestMatchers("/", "/login",
                    "/register", "/chkid-dup/{username}", "/find/**",
                    "/css/**", "/images/**", "/js/**", "/api/**", "/upload/**",
                    "/review", "/review/detail/**", "/community", "/community/{id}",
                    "/feed"
                ) // 클라이언트 요청이 이것과 일치한다면
                .permitAll() // 접근을 허가
                .requestMatchers(
                    "/review/write", "/review/edit/**",
                    "/community/**", "/reply/**", "/feed/**", "/mypage/**"
                ).hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated() // 위에서 허가하지 않은 요청은 인가와 인증을 받음
            )
            .formLogin(form -> form
                .loginPage("/login") // 인증이 필요한 경우 GET 요청
                // 클라이언트 요청이 POST 방식에 /login이라면 시큐리티가 인증 받도록 처리
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true) // 로그인 성공 시 요청
                .failureUrl("/login?error") // 로그인 실패 시 요청 (아이디 혹은 비밀번호 틀림)
            ).logout(logout -> logout
                .logoutUrl("/logout") // 스프링 시큐리티가 로그아웃 처리
                .invalidateHttpSession(true) // 서버에 저장된 HttpSession 제거
                .deleteCookies("JSESSION") // 클라이언트 내 세션 식별 쿠키 삭제
                .logoutSuccessUrl("/login?logout") // 로그아웃 완료 후 로그인 페이지로 리다이렉트
            );

        return http.build();
    }
}
