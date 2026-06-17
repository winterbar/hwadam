package com.miles.beauminity.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberProfileMapper {
    public void insertMemberProfile(String username);
}
