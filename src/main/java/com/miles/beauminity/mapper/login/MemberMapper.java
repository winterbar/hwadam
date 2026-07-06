package com.miles.beauminity.mapper.login;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.MemberVO;

@Mapper
public interface MemberMapper {
    public long findMemberById(String username);
    public void insertMember(MemberVO memberVO);
    public int updateMember(MemberVO memberVO);
    public int updatePassword(MemberVO memberVO);
    public MemberVO findLoginId(String username);
    public String findGradeName(String gradeId);
    public String findPasswordById(String username);
    

}
