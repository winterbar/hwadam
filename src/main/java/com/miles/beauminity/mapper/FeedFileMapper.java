package com.miles.beauminity.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedFileVO;

@Mapper
public interface FeedFileMapper {
    void postFile(FeedFileVO feedFileVO);
    
}
