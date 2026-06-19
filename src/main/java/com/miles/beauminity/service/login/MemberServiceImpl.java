package com.miles.beauminity.service.login;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miles.beauminity.mapper.FeedMapper;
import com.miles.beauminity.mapper.MasterBoardMapper;
import com.miles.beauminity.mapper.MemberMapper;
import com.miles.beauminity.mapper.MemberProfileMapper;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberProfileMapper memberProfileMapper;
    private final MasterBoardMapper MasterBoardMapper;
    private final FeedMapper feedMapper;
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

    // 로그인한 사용자의 정보 및 게시글 별로 등록된 글 개수를 반환
    @Override
    public MyPageVO getMemberInfo(String username) {
        MemberVO member = memberMapper.findLoginId(username);
        MyPageVO memberInfo = new MyPageVO();

        // 회원 정보
        memberInfo.setMember(member);
        // 회원의 등급 이름
        memberInfo.setGradeName(memberMapper.findGradeName(member.getGradeId()));
        // 후기, 질문, 정보공유 게시판별 회원이 등록한 글 개수
        List<Map<String, Object>> resultMap = MasterBoardMapper.countBoard(username);
        for(Map<String, Object> result : resultMap) {
            String type = (String) result.get("board_type");
            Long count = (Long) result.get("count");

            if("review".equals(type)) memberInfo.setReviewCnt(count);
            else if("qna".equals(type)) memberInfo.setQnaCnt(count);
            else if("infoshare".equals(type)) memberInfo.setInfoshareCnt(count);
        }
        // 회원이 등록한 피드 수
        memberInfo.setFeedCnt(feedMapper.countFeed(username));
        return memberInfo;
    }
}
