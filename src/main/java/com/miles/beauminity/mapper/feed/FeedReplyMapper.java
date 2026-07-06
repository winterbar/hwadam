package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedReplyVO;

@Mapper
public interface FeedReplyMapper {
    public void postReply(FeedReplyVO feedReplyVO);

    public List<FeedReplyVO> getReply(Long feedReplyVO);

    public List<String> getFeedReplyList(Long feedId);

    public int getReplyCnt(FeedReplyVO feedReplyVO);

    public void increaseReply(FeedReplyVO feedReplyVO);

    public void updateReply(FeedReplyVO feedReplyVO);

    public void updateDeleteReply(Long replyId);

    public Long hasChildren(Long replyId);

    public Long deleteReply(Long replyId);

    public int parentsReplyCnt(Long replyId);

    public Long parentsDelete(Long replyId);

    public Long parentsReplyFindId(Long replyId);

}
