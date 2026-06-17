package com.miles.beauminity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedMapper {
    void postFeed(FeedVO feedVO);
    
}
