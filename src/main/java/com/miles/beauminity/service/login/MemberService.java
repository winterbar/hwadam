package com.miles.beauminity.service.login;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;

public interface MemberService {
    public boolean findMember(String username);
    public void registerMember(MemberVO memberVO);
    public boolean updateMember(MemberVO memberVO);
    public boolean updatePassword(MemberVO memberVO);
    public MyPageVO getMemberInfo(String username);
    public void updateMemberProfile(CustomUserDetails loginMember, MultipartFile file);
    public void resetMemberProfile(String username);
    public boolean findPassword(String username, String password);
    public List<FeedVO> getFeedList(String username);
    public List<QnaBoardCompleteVO> getQnaList(String username);
}
