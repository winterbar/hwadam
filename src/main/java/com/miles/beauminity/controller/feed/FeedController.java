package com.miles.beauminity.controller.feed;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

import lombok.AllArgsConstructor;




@Controller
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/feed")
    public String getMainFeed() {
        return "feed/list";
    }
    @GetMapping("/feed/write")
    public String getWriteFeed() {
        return "feed/write";
    }
    @PostMapping("/feed/write")
    public String postWriteFeed(
        @ModelAttribute FeedVO feedVO,
        @RequestParam("files") MultipartFile[] files,
        @RequestParam("tagNames") List<TagVO> tagNames
    ) {
        feedService.postFeed(feedVO,files,tagNames);
        return "redirect:/feed";
    }
}
