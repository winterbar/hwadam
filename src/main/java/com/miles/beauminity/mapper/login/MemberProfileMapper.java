package com.miles.beauminity.mapper.login;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.login.MyPageFileVO;

@Mapper
public interface MemberProfileMapper {
    public void insertMemberProfile(String username);
    public void updateMemberProfile(MyPageFileVO myPageFileVO);
    public MyPageFileVO findMemberProfile(String username);
    public void resetMemberProfile(String username);
    
}
