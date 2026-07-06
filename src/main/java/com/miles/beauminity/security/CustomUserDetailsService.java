package com.miles.beauminity.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.miles.beauminity.mapper.admin.AdminMemberMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.vo.admin.AdminMemberVO;

import lombok.RequiredArgsConstructor;

// 사용자 정보를 데이터베이스로부터 가져와 클라이언트가 보낸 비밀번호 일치를 판단
// 이런 역할을 하는 스프링에서 제공하는 UserDetailsService 객체를 구현받아 사용
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminMemberMapper adminMemberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminMemberVO member = adminMemberMapper.getMemberInfo(username); // 로그인을 시도하는 사용자
        
        // 입력한 아이디로 가입되어 있지 않을 경우
        if(member == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
        }

        // 탈퇴한 아이디인 경우
        // 보안을 위해 탈퇴한 계정임을 알리지 않고 정보 불일치 문구를 출력
        if("deleted".equals(member.getStatus())) {
            throw new BadCredentialsException("아이디나 비밀번호가 일치하지 않습니다.");
        }

        // 스프링이 member 객체의 타입을 알 수 있게 userDetails 객체로 랩핑
        return new CustomUserDetails(member);
    }
    
}
