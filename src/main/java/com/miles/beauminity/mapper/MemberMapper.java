package com.miles.beauminity.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.login.MemberVO;

@Mapper
public interface MemberMapper {
    public long findMemberById(String username);
    public void insertMember(MemberVO memberVO);
    public MemberVO findLoginId(String username);
    public String findGradeName(String gradeId);
}
