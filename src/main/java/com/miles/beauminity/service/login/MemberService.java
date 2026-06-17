package com.miles.beauminity.service.login;

import com.miles.beauminity.vo.login.MemberVO;

public interface MemberService {
    public boolean findMember(String username);
    public void registerMember(MemberVO memberVO);
}
