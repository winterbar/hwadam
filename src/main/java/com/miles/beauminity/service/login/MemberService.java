package com.miles.beauminity.service.login;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageVO;

public interface MemberService {
    public boolean findMember(String username);
    public void registerMember(MemberVO memberVO);
    public MyPageVO getMemberInfo(String username);
    public void updateMemberProfile(CustomUserDetails loginMember, MultipartFile file);
    public void resetMemberProfile(String username);
}
