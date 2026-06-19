package com.miles.beauminity.service.login;

import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.login.MyPageVO;

public interface MemberService {
    public boolean findMember(String username);
    public void registerMember(MemberVO memberVO);
    public MyPageVO getMemberInfo(String username);
}
