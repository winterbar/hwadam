package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedMapper {
    public void postFeed(FeedVO feedVO);

    public List<FeedVO> getFeedList(String username);

    public FeedVO loadFeedData(Long feedId);

    public FeedVO loadTagData(Long feedId);

    public void deleteFeedId(Long feedId);

    public long countFeed(String username);

    public void updateFeed(FeedVO feedVO);

    public void cancleLikeCnt(FeedLikeVO feedLikeVO);

    public void increaseLikeCnt(FeedLikeVO feedLikeVO);

    public int getLikeCnt(@Param("feedId") Long feedId);

    public List<FeedVO> getShareFeedlist(Long feedId);

    public List<FeedVO> getMyFeedList(String username);

    public void withdrawFeed(String username);

}
