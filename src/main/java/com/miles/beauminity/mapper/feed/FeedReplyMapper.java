package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedReplyVO;

@Mapper
public interface FeedReplyMapper {
    void postReply(FeedReplyVO feedReplyVO);

    List<FeedReplyVO> getReply(Long feedReplyVO);

    List<String> getFeedReplyList(Long feedId);

    int getReplyCnt(FeedReplyVO feedReplyVO);

    void increaseReply(FeedReplyVO feedReplyVO);

    void updateReply(FeedReplyVO feedReplyVO);

    void updateDeleteReply(Long replyId);

    Long hasChildren(Long replyId);

    Long deleteReply(Long replyId);

    int parentsReplyCnt(Long replyId);

    Long parentsDelete(Long replyId);

    Long parentsReplyFindId(Long replyId);

}
