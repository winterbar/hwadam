package com.miles.beauminity.service.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miles.beauminity.mapper.MemberMapper;
import com.miles.beauminity.mapper.MemberProfileMapper;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberProfileMapper memberProfileMapper;
    private final PasswordEncoder passwordEncoder;

    // 사용자가 입력한 아이디로 가입된 계정이 있는지 확인
    @Override
    public boolean findMember(String username) {
        if(memberMapper.findMemberById(username) > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 새로운 회원 정보를 등록 (회원가입)
    // member 테이블엔 회원 정보, member_profile에는 프로필 사진 정보
    // member와 member_profile은 식별 관계로 member 테이블에 튜플이 정상적으로 추가 되어야
    // member_profile에 튜플을 추가할 수 있어 트랜잭션 제어 처리로 구현
    @Override
    @Transactional(rollbackFor = Exception.class) // RunTimeException 외에도 모든 예외를 감지
    public void registerMember(MemberVO memberVO) {
        // 아이디를 소문자로 변환
        if (memberVO.getUsername() != null) {
            memberVO.setUsername(convertLowerId(memberVO.getUsername()));
        }
        // 패스워드 암호화
        if (memberVO.getPassword() != null) {
            memberVO.setPassword(passwordEncoder.encode(memberVO.getPassword()));
        }
        memberMapper.insertMember(memberVO);
        memberProfileMapper.insertMemberProfile(memberVO.getUsername());
    }
    
    // 입력된 아이디를 모두 소문자로 변환
    // 아이디는 대게 소문자만 사용하기 때문에 변환해준다.
    private String convertLowerId(String username) {
        String converted_username = username.trim().toLowerCase();
        return converted_username;
    }
}
