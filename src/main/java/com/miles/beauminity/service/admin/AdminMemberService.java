package com.miles.beauminity.service.admin;

import java.util.List;

import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

public interface AdminMemberService {
    public long countMembers(AdminPageVO adminPageVO);
    public List<AdminMemberVO> searchMembers(AdminPageVO adminPageVO);
    public void registerMember(AdminMemberVO adminMemberVO);
    public AdminMemberVO getMemberDetails(String username);
    public void modifyMemberInfo(AdminMemberVO adminMemberVO);
}
