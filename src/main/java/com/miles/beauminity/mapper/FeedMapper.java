package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedMapper {
    void postFeed(FeedVO feedVO);

    List<FeedVO> getFeedList();

    FeedVO loadFeedData(Long feedId);

    FeedVO loadTagData(Long feedId);

    void deleteFeed(Long feedId);

}
