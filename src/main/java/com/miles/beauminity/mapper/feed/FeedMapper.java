package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedMapper {
    void postFeed(FeedVO feedVO);

    List<FeedVO> getFeedList(String username);

    FeedVO loadFeedData(Long feedId);

    FeedVO loadTagData(Long feedId);

    void deleteFeedId(Long feedId);

    long countFeed(String username);

    void updateFeed(FeedVO feedVO);

    void cancleLikeCnt(FeedLikeVO feedLikeVO);

    void increaseLikeCnt(FeedLikeVO feedLikeVO);

    int getLikeCnt(FeedLikeVO feedLikeVO);

}
