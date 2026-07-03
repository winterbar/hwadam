package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.MemberVO;

public interface FeedService {
    void postFeed(FeedVO feedVO, MultipartFile[] files, List<String> tagNames);

    List<String> getTagNameList();

    List<FeedVO> getFeedList(String username);

    FeedVO loadFeedData(Long feedId);

    void deleteFeedId(Long feedId);

    void updateFeed(Long feedId, FeedVO feedVO, MultipartFile[] files, List<String> existingImages,
            List<String> tagNames);

    int getFeedLike(Boolean liked, FeedLikeVO feedLikeVO);

    List<FeedReplyVO> getFeedReply(FeedReplyVO feedReplyVO);

    List<FeedReplyVO> updateReply(FeedReplyVO feedReplyVO);

    void deleteReply(Long replyId);
    
    List<FeedVO> getShareFeedlist(Long feedId);
}