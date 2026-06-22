package com.miles.beauminity.controller.feed;

import java.lang.reflect.Member;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    // 피드 버튼 눌렀을때 화면 호출
    @GetMapping("/feed")
    public String getMainFeed(Model model) {
        // 작성된 피드 리스트로 가져와서 화면 띄우기
        List<FeedVO> feedList = feedService.getFeedList();
        // 저장되어있는 전체 태그 리스트로 가져와서 화면 띄우기
        List<String> tagList = feedService.getTagNameList();
        model.addAttribute("tagList", tagList);
        model.addAttribute("feedList", feedList);

        return "feed/list";
    }

    // 피드 작성하기 버튼 눌렀을 때 화면 호출
    @GetMapping("/feed/write")
    public String getWriteFeed() {
        return "feed/write";
    }

    // 피드 작성 후 등록 눌렀을 때 저장 및 화면 호출
    @PostMapping("/feed/write")
    public String postWriteFeed(
            @ModelAttribute FeedVO feedVO,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("tagNames") List<String> tagNames,
            Principal principal) {
        String username = principal.getName();
        feedVO.setUsername(username);
        // 피드 내용과 사진,해시태그 저장
        feedService.postFeed(feedVO, files, tagNames);
        return "redirect:/feed";
    }

    // 수정하기 눌렀을 때 화면 호출
    @GetMapping("/feed/{feedId}/edit")
    public String loadFeed(@PathVariable("feedId") Long feedId, Model model) {
        FeedVO feed = feedService.loadFeedData(feedId);
        model.addAttribute("feed", feed);
        return "feed/edit";
    }

    // 수정 완료 눌렀을 때
    @PostMapping("/feed/{feedId}/edit")
    public String updateFeed(@PathVariable("feedId") Long feedId,
            @ModelAttribute FeedVO feedVO,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("existingImages") List<String> existingImages, // 기존 사진
            @RequestParam("tagNames") List<String> tagNames) {

        feedVO.setFeedId(feedId);

        String content = feedVO.getFeedContent();
        String infoLink = feedVO.getInfoLink();

        // 내용이 null이라면 빈 문자열로 변환
        if (content == null) {
            content = "";
        }

        if (infoLink != null && !infoLink.trim().isEmpty()) {
            feedVO.setFeedContent(content.trim() + "," + infoLink.trim());
        } else {
            feedVO.setFeedContent(content.trim());
        }

        feedService.updateFeed(feedId, feedVO, files, existingImages, tagNames);

        return "redirect:/feed";
    }

    // 삭제하기 눌렀을 때 화면 호출
    @GetMapping("/feed/{feedId}/delete")
    public String deleteFeedId(@PathVariable("feedId") Long feedId) {
        feedService.deleteFeedId(feedId);
        return "redirect:/feed";
    }

    @PostMapping("/feed/{feedId}/reply")
    public String postReply(@PathVariable("feedId") Long feedId, FeedReplyVO feedReplyVO,
            Principal principal) {
        String username = principal.getName();
        feedReplyVO.setUsername(username);
        feedReplyVO.setFeedId(feedId);
        feedService.postReply(feedReplyVO);
        return "redirect:/feed";
    }
}
