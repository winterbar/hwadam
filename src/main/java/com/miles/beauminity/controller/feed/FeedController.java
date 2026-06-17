package com.miles.beauminity.controller.feed;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.vo.feed.FeedVO;

import lombok.AllArgsConstructor;
import org.springframework.ui.Model;




@Controller
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;
    // 피드 버튼 눌렀을때 화면 호출
    @GetMapping("/feed")
    public String getMainFeed(Model model) {
        List<String> feedList = feedService.getFeedList();
        //저장되어있는 태그 리스트로 가져와서 화면 띄우기
        List<String> tagList = feedService.getTagNameList();
        model.addAttribute("tagList",tagList);
        model.addAttribute("feedList",feedList);
        return "feed/list";
    }
    // 피드 작성하기 버튼 눌렀을때 화면 호출
    @GetMapping("/feed/write")
    public String getWriteFeed() {
        return "feed/write";
    }
    // 피드 작성 후 등록 눌렀을때 저장 및 화면 호출
    @PostMapping("/feed/write")
    public String postWriteFeed(
        @ModelAttribute FeedVO feedVO,
        @RequestParam("files") MultipartFile[] files,
        @RequestParam("tagNames") List<String> tagNames
    ) {
        feedService.postFeed(feedVO,files,tagNames);
        return "redirect:/feed";
    }
}
