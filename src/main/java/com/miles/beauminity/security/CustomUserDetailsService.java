package com.miles.beauminity.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.miles.beauminity.mapper.MemberMapper;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.RequiredArgsConstructor;

// 사용자 정보를 데이터베이스로부터 가져와 클라이언트가 보낸 비밀번호 일치를 판단
// 이런 역할을 하는 스프링에서 제공하는 UserDetailsService 객체를 구현받아 사용
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberVO member = memberMapper.findLoginId(username); // 로그인을 시도하는 사용자
        if(member == null) { // 입력한 아이디로 가입되어 있지 않을 경우
            throw new UsernameNotFoundException("가입된 사용자가 아닙니다.");
        }
        // 스프링이 member 객체의 타입을 알 수 있게 userDetails 객체로 랩핑
        return new CustomUserDetails(member);
    }
    
}
