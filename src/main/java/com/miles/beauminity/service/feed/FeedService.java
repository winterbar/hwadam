package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.FeedVO;
import com.miles.beauminity.vo.TagVO;

public interface FeedService {
    void postFeed(FeedVO feedVO,MultipartFile[] files,List<TagVO> tagNames);
}