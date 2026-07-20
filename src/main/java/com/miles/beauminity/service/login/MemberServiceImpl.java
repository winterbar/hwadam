package com.miles.beauminity.service.login;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.board.MasterBoardMapper;
import com.miles.beauminity.mapper.feed.FeedFileMapper;
import com.miles.beauminity.mapper.feed.FeedMapper;
import com.miles.beauminity.mapper.feed.TagMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.mapper.login.MemberProfileMapper;
import com.miles.beauminity.mapper.qna_board.QnaBoardMapper;
import com.miles.beauminity.mapper.review_board.ReviewBoardMapper;
import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.security.CustomUserDetailsService;
import com.miles.beauminity.util.MemberFileUtil;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.FeedbackVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageFileVO;
import com.miles.beauminity.vo.login.MyPageVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberProfileMapper memberProfileMapper;
    private final MasterBoardMapper MasterBoardMapper;
    private final FeedMapper feedMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final FeedFileMapper feedFileMapper;
    private final TagMapper tagMapper;
    private final QnaBoardMapper qnaBoardMapper;
    private final MasterBoardMapper masterBoardMapper;
    private final ReviewBoardMapper reviewBoardMapper;

    // 사용자가 입력한 아이디로 가입된 계정이 있는지 확인
    @Override
    public boolean findMember(String username) {
        return (memberMapper.findMemberById(username) > 0) ? true : false;
    }
    
    // 사용자가 입력한 비밀번호가 일치하는지 확인
    @Override
    public boolean findPassword(String username, String password) {
        return passwordEncoder.matches(password, memberMapper.findPasswordById(username));
    }

    // 사용자가 입력한 이메일로 가입된 계정이 있는지 확인
    @Override
    public boolean findEmail(String email) {
        return (memberMapper.findMemberByEmail(email) > 0) ? true : false;
    }

    // 사용자가 입력한 아이디와 이메일로 가입된 계정이 있는지 확인
    public boolean findEmailAndId(String username, String email) {
        MemberVO member = new MemberVO();
        member.setUsername(username);
        member.setEmail(email);
        return (memberMapper.findMemberByEmailUsername(member) > 0) ? true : false;
    }

    // 새로운 회원 정보를 등록 (회원가입)
    // member 테이블엔 회원 정보, member_profile에는 프로필 사진 정보
    // member와 member_profile은 식별 관계로 member 테이블에 튜플이 정상적으로 추가 되어야
    // member_profile에 튜플을 추가할 수 있어 트랜잭션 제어 처리로 구현
<<<<<<< HEAD
    @Override
    @Transactional(rollbackFor = Exception.class) // RunTimeException 외에도 모든 예외를 감지
=======
    @Transactional(rollbackFor = Exception.class) // RunTimeException 외에도 모든 예외를 감지
    @Override
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
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

        // 포인트 지급
        memberVO.setPoint(100);
        memberMapper.updatePoint(memberVO);
    }
    
    // 입력된 아이디를 모두 소문자로 변환
    // 아이디는 대게 소문자만 사용하기 때문에 변환해준다.
    private String convertLowerId(String username) {
        String converted_username = username.trim().toLowerCase();
        return converted_username;
    }

    // 사용자의 회원 정보 수정
<<<<<<< HEAD
=======
    @Transactional
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
    @Override
    public boolean updateMember(MemberVO memberVO) {
        int updated = memberMapper.updateMember(memberVO);

        /// 마이페이지에서 닉네임을 수정하면 로그인 상태창에선 변경되지 않는 문제 발생
        /// 해결 방법 : 로그인할 때 인증 정보를 변경된 내용으로 갱신하는 것으로 해결
        // 기존의 인증 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 수정된 회원 정보를 반영한 새로운 CustomUserDetails 생성
        CustomUserDetails updatedUser =
            (CustomUserDetails) customUserDetailsService.loadUserByUsername(auth.getName());
        // 새로 생성한 CustomUserDetails를 바탕으로 한 새로운 인증 토큰 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            updatedUser,
            auth.getCredentials(),
            updatedUser.getAuthorities()
        );
        // SecurityContext에 새로운 인증 토큰 등록
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return updated > 0;
    }

    // 사용자의 계정 비밀번호 수정
<<<<<<< HEAD
=======
    @Transactional
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
    @Override
    public boolean updatePassword(MemberVO memberVO) {
        // 변경할 패스워드 암호화
        if(memberVO.getPassword() != null) {
            memberVO.setPassword(passwordEncoder.encode(memberVO.getPassword()));
        }
        int updated = memberMapper.updatePassword(memberVO);
        return updated > 0;
    }

    // 사용자의 프로필 사진 수정
<<<<<<< HEAD
=======
    @Transactional
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
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
<<<<<<< HEAD
=======
    @Transactional
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
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
    public MyPageVO getMemberInfo(String username) {
        MemberVO member = memberMapper.findLoginId(username);
        MyPageVO memberInfo = new MyPageVO();

        // 회원 정보
        memberInfo.setMember(member);
<<<<<<< HEAD
        // 등급 이름
        memberInfo.setGradeName(memberMapper.findGradeName(member.getGradeId()));
=======
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
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

    // 이메일로 가입된 회원 정보 검색
    public List<MemberVO> findSignedMembers(String email) {
        List<MemberVO> members = memberMapper.findSignedIdsByEmail(email);
        for(MemberVO member : members) { maskUsername(member); }
        return members;
    }

    // 아이디 마스킹
    private void maskUsername(MemberVO memberVO) {
        if (memberVO != null) {
            String username = memberVO.getUsername();
            String maskedUsername = username.substring(0, 4)
                                        + "*".repeat(username.length() -3);
            memberVO.setUsername(maskedUsername);
        }
    }

    // 해당 사용자의 모든 피드 리스트 가져오기    
    @Override
    public List<FeedVO> getFeedList(String username) {
        List<FeedVO> feedList = feedMapper.getMyFeedList(username);
        
       for (FeedVO feed : feedList) {
            // 특정 피드 아이디에 해당하는 해시태그를 가져와서 리스트로 저장
            List<String> feedTagList = tagMapper.getFeedTagList(feed.getFeedId());
            // 특정 피드 아이디에 해당하는 사진 가져와서 리스트로 저장
            List<String> feedFileList = feedFileMapper.getFeedFileList(feed.getFeedId());

            feed.setFeedFileList(feedFileList);
            feed.setFeedTagList(feedTagList);

        }

        return feedList;
    }
    //해당 사용자가 작성한 모든 커뮤니티 리스트 가져오기
    @Override
    public List<QnaBoardCompleteVO> getCommunityList(String username) {
        
        return qnaBoardMapper.getCommunityList(username);
    }
    @Override
    public List<ReviewBoardVO> getReviewList(String username) {
        return reviewBoardMapper.getReviewList(username);
    }

    // 탈퇴한 회원 username 변경 및 상태 변경
    @Transactional
    @Override
    public void withdraw(String username, FeedbackVO feedbackVO) {
        feedMapper.withdrawFeed(username);
        masterBoardMapper.withdrawBoard(username);
        memberMapper.withdrawMember(username);
        //탈퇴한 사유 저장
        memberMapper.feedback(feedbackVO);
    }


}