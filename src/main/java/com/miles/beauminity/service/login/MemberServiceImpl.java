package com.miles.beauminity.service.login;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.board.MasterBoardMapper;
import com.miles.beauminity.mapper.feed.FeedMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.mapper.login.MemberProfileMapper;
import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.util.MemberFileUtil;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageFileVO;
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

    // 사용자의 프로필 사진 수정
    @Override
    public void updateMemberProfile(CustomUserDetails loginMember, MultipartFile file) {
        // 등록한 이미지가 존재할 경우 해당 이미지 삭제
        MyPageFileVO profile = memberProfileMapper.findMemberProfile(loginMember.getUsername());
        if(profile.getSavedName() != null) {
            MemberFileUtil.deleteFiles(profile.getFilePath(), profile.getSavedName());
        }
        // 새로운 이미지 저장
        String uploadDir = "C:/upload/profile";
        MyPageFileVO savedFile = MemberFileUtil.saveFiles(file, uploadDir);
        if(savedFile != null) {
            savedFile.setUsername(loginMember.getUsername()); // 로그인된 회원 아이디
        }
        memberProfileMapper.updateMemberProfile(savedFile);
    }

    // 사용자의 프로필 사진을 기본 프로필로 수정
    @Override
    public void resetMemberProfile(String username) {
        // 등록한 이미지가 존재할 경우 해당 이미지 삭제
        MyPageFileVO profile = memberProfileMapper.findMemberProfile(username);
        if(profile.getSavedName() != null) {
            MemberFileUtil.deleteFiles(profile.getFilePath(), profile.getSavedName());
        }
        memberProfileMapper.resetMemberProfile(username);
    }

    // 사용자의 정보(회원 정보, 프로필 사진, 작성된 글 수 등) 검색
    @Override
    @Transactional(readOnly = true)
    public MyPageVO getMemberInfo(String username) {
        MemberVO member = memberMapper.findLoginId(username);
        MyPageVO memberInfo = new MyPageVO();

        // 회원 정보
        memberInfo.setMember(member);
        // 등급 이름
        memberInfo.setGradeName(memberMapper.findGradeName(member.getGradeId()));
        // 프로필 사진 정보
        MyPageFileVO profile = memberProfileMapper.findMemberProfile(username);
        String filePath = "C:/upload/profile/" + profile.getSavedName();
        File file = new File(filePath);
        if(file.exists()) { // 실제로 서버 하드에 사진이 저장되어 있는지 확인
            memberInfo.setProfile(profile);
        }
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
