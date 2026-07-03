package com.miles.beauminity.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

@Mapper
public interface AdminMemberMapper {
    public long countMembers(AdminPageVO AdminPageVO);
    public List<AdminMemberVO> selectMembers(AdminPageVO AdminPageVO);
    public void insertMember(AdminMemberVO adminMemberVO);
    public AdminMemberVO getMemberInfo(String username);
    public void updateMemberInfo(AdminMemberVO adminMemberVO);
}
