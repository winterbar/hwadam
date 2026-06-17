package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

public interface FeedService {
    void postFeed(FeedVO feedVO,MultipartFile[] files,List<String> tagNames);
    List<String> getTagNameList();
    List<String> getFeedList();
}