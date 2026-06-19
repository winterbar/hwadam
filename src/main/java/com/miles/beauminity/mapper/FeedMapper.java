package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedMapper {
    void postFeed(FeedVO feedVO);

    List<FeedVO> getFeedList();
    List<String> getFeedTagList(Long feedId);
    List<String> getFeedFileList(Long feedId);
    FeedVO loadFeedData(Long feedId);
    FeedVO loadTagData(Long feedId);
    void deleteFeed(Long feedId);
    long countFeed(String username);
}
