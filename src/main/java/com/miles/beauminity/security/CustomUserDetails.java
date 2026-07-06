package com.miles.beauminity.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.miles.beauminity.vo.admin.AdminMemberVO;

public class CustomUserDetails implements UserDetails {

     // 로그인한 회원 정보를 저장
    private final AdminMemberVO member;

    // MemberVO를 UserDetails 형태로 변환하기 위한 생성자
    public CustomUserDetails(AdminMemberVO member) {
        this.member = member;
    }

    // 로그인한 회원 정보를 꺼내기 위한 메서드
    public AdminMemberVO getMember() {
        return member;
    }

    /**
     * 사용자의 권한(Role) 정보를 반환
     * Spring Security가 인가(Authorization) 처리 시 사용한다.
     *
     * 예)
     * member.getRole() = "USER"
     * → ROLE_USER 반환
     *
     * hasRole("USER")
     * hasAuthority("ROLE_USER")
     * 와 같은 권한 검사에 사용된다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + member.getRole())
        );
    }

    /**
     * 암호화된 비밀번호 반환
     * 로그인 시 PasswordEncoder.matches()에서 사용된다.
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자 식별자 반환
     * 로그인 시 입력한 아이디와 비교하는 값이다.
     * 일반적으로 loginId 또는 username을 반환한다.
     */
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    /* 사용자 닉네임 반환 */
    public String getNickname() {
        return member.getNickname();
    }

    /* 사용자 포인트 변환 */
    public int getPoint() {
        return member.getPoint();
    }

    /**
     * 계정 만료 여부
     * true  : 계정 사용 가능
     * false : 계정 만료
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부
     * true  : 계정 사용 가능
     * false : 계정 잠금
     *
     * 로그인 실패 횟수 초과 시 잠금 기능 구현 시 사용 가능
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true  : 비밀번호 사용 가능
     * false : 비밀번호 만료
     *
     * 일정 기간마다 비밀번호 변경 정책 구현 시 사용 가능
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * true  : 활성 계정
     * false : 비활성 계정
     *
     * 이메일 인증 전 사용자 차단 등에 활용 가능
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
