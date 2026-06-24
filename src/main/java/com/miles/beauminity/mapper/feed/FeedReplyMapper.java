package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedReplyVO;

@Mapper
public interface FeedReplyMapper {
    void postReply(FeedReplyVO feedReplyVO);

    List<FeedReplyVO> getReply(FeedReplyVO feedReplyVO);

    List<String> getFeedReplyList(Long feedId);

}
