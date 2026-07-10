package com.miles.beauminity.mapper.login;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.login.FeedbackVO;
import com.miles.beauminity.vo.login.MemberVO;

@Mapper
public interface MemberMapper {
    public long findMemberById(String username);
    public long findMemberByEmail(String email);
    public void insertMember(MemberVO memberVO);
    public int updateMember(MemberVO memberVO);
    public int updatePassword(MemberVO memberVO);
    public MemberVO findLoginId(String username);
    public String findGradeName(String gradeId);
    public String findPasswordById(String username);
    public void withdrawMember(String username);
    public void feedback(FeedbackVO feedbackVO);
}
