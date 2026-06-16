package com.miles.beauminity.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.FeedFileVO;

@Mapper
public interface FeedFileMapper {
    void postFile(FeedFileVO feedFileVO);
    
}
