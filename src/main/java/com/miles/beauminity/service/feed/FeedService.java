package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.MemberVO;

public interface FeedService {
    public void postFeed(FeedVO feedVO, MultipartFile[] files, List<String> tagNames);

    public List<String> getTagNameList();

    public List<FeedVO> getFeedList(String username);

    public FeedVO loadFeedData(Long feedId);

    public void deleteFeedId(Long feedId);

    public void updateFeed(Long feedId, FeedVO feedVO, MultipartFile[] files, List<String> existingImages,
            List<String> tagNames);

    public int getFeedLike(Boolean liked, FeedLikeVO feedLikeVO);

    public List<FeedReplyVO> getFeedReply(FeedReplyVO feedReplyVO);

    public List<FeedReplyVO> updateReply(FeedReplyVO feedReplyVO);

    public void deleteReply(Long replyId);
    
    public List<FeedVO> getShareFeedlist(Long feedId);
}